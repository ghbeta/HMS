package controllers.account;

import models.Token;
import models.User;
import play.mvc.Http;
import utils.AppException;
import utils.Mail;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.account.reset.ask;

import java.net.MalformedURLException;

import static play.data.Form.form;

public class Reset extends Controller {

    public static class AskForm {
        @Constraints.Required
        public String email;
    }

    public static class ResetForm {
        @Constraints.Required
        public String inputPassword;
    }

    /**
     * Display the reset password form.
     *
     * @return reset password form
     */
    public static Result ask() {
        Form<AskForm> askForm = form(AskForm.class);
        return ok(ask.render(askForm));
    }

    /**
     * Run ask password.
     *
     * @return reset password form if error, runAsk render otherwise
     */
    public static Result runAsk() {
        Form<AskForm> askForm = form(AskForm.class).bindFromRequest();

        if (askForm.hasErrors()) {
            flash("danger", Messages.get("signup.valid.email"));
            return badRequest(ask.render(askForm));
        }

        final String email = askForm.get().email;
        Logger.debug("runAsk: email = " + email);
        User user = User.findByEmail(email,"global");
        Logger.debug("runAsk: user = " + user);

        // If we do not have this email address in the list, we should not expose this to the user.
        // This exposes that the user has an account, allowing a user enumeration attack.
        // See http://www.troyhunt.com/2012/05/everything-you-ever-wanted-to-know.html for details.
        // Instead, email the person saying that the reset failed.
        if (user == null) {
            Logger.debug("No user found with email " + email);
            sendFailedPasswordResetAttempt(email);
            return ok(views.html.account.reset.runAsk.render());
        }

        Logger.debug("Sending password reset link to user " + user);

        try {
            Token.sendMailResetPassword(user,"global");
            return ok(views.html.account.reset.runAsk.render());
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("danger", Messages.get("error.technical"));
        }
        return badRequest(ask.render(askForm));
    }

    /**
     * Sends an email to say that the password reset was to an invalid email.
     *
     * @param email the email address to send to.
     */
    private static void sendFailedPasswordResetAttempt(String email) {
        String subject = Messages.get("mail.reset.fail.subject");
        String message = Messages.get("mail.reset.fail.message", email);

        Mail.Envelop envelop = new Mail.Envelop(subject, message, email);
        Mail.sendMail(envelop);
    }

    public static Result reset(String token) {

        if (token == null) {
            flash("danger", Messages.get("error.technical"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password,"global");
        if (resetToken == null) {
            flash("danger", Messages.get("error.technical"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        if (resetToken.isExpired()) {
            resetToken.delete();
            flash("danger", Messages.get("error.expiredresetlink"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        //Form<ResetForm> resetForm = form(ResetForm.class);

        User current=User.findByEmail(ctx().session().get("email"), "global");

        return ok(views.html.account.reset.reset.render(current, token));

    }

    /**
     * @return reset password form
     */
    public static Result runReset(String token) {
        Form<ResetForm> resetForm = form(ResetForm.class).bindFromRequest();
        User current=User.findByEmail(ctx().session().get("email"), "global");
        if (resetForm.hasErrors()) {
            flash("danger", Messages.get("signup.valid.password"));
            return badRequest(views.html.account.reset.reset.render(null, token));
        }

        try {
            Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password,"global");
            if (resetToken == null) {
                flash("danger", Messages.get("error.technical"));
                return badRequest(views.html.account.reset.reset.render(current, token));
            }

            if (resetToken.isExpired()) {
                resetToken.delete();
                flash("danger", Messages.get("error.expiredresetlink"));
                return badRequest(views.html.account.reset.reset.render(current, token));
            }

            // check email
            User user = User.findById(resetToken.userId,"global");
            if (user == null) {
                // display no detail (email unknown for example) to
                // avoir check email by foreigner
                flash("danger", Messages.get("error.technical"));
                return badRequest(views.html.account.reset.reset.render(current, token));
            }

            String password = resetForm.get().inputPassword;
            user.changePassword(password,"global");

            // Send email saying that the password has just been changed.
            sendPasswordChanged(user);
            resetToken.delete("global");
            flash("success", Messages.get("resetpassword.success"));
            return ok(views.html.account.reset.reset.render(current, token));
        } catch (AppException e) {
            flash("danger", Messages.get("error.technical"));
            return badRequest(views.html.account.reset.reset.render(current, token));
        } catch (EmailException e) {
            flash("danger", Messages.get("error.technical"));
            return badRequest(views.html.account.reset.reset.render(current, token));
        }

    }

    /**
     * Send mail with the new password.
     *
     * @param user user created
     * @throws EmailException Exception when sending mail
     */
    private static void sendPasswordChanged(User user) throws EmailException {
        String subject = Messages.get("mail.reset.confirm.subject");
        String message = Messages.get("mail.reset.confirm.message");
        Mail.Envelop envelop = new Mail.Envelop(subject, message, user.email);
        Mail.sendMail(envelop);
    }
}
