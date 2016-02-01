package controllers.account.settings;

import Permission.Secureddefault;
import models.Token;
import models.User;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.settings.email;

import java.net.MalformedURLException;

import static play.data.Form.form;


@Security.Authenticated(Secureddefault.class)
public class Email extends Controller {

    public static class AskForm {
        @Constraints.Required
        public String email;
        public AskForm() {}
        AskForm(String email) {
            this.email = email;
        }
    }

    /**
     * Password Page. Ask the user to change his password.
     *
     * @return index settings
     */
    public static Result index() {
        User user = User.findByEmail(request().username(),"global");
        Form<AskForm> askForm = form(AskForm.class);
        askForm = askForm.fill(new AskForm(user.email));
        return ok(email.render(User.findByEmail(request().username(),"global"), askForm));
    }

    /**
     * Send a mail to confirm.
     *
     * @return email page with flash error or success
     */
    public static Result runEmail() {
        Form<AskForm> askForm = form(AskForm.class).bindFromRequest();
        User user = User.findByEmail(request().username(),"global");

        if (askForm.hasErrors()) {
            flash("danger", Messages.get("signup.valid.email"));
            return badRequest(email.render(user, askForm));
        }

        try {
            String mail = askForm.get().email;
            Token.sendMailChangeMail(user, mail,"global");
            flash("success", Messages.get("changemail.mailsent"));
            return ok(email.render(user, askForm));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("danger", Messages.get("error.technical"));
        }
        return badRequest(email.render(user, askForm));
    }

    /**
     * Validate a email.
     *
     * @return email page with flash error or success
     */
    public static Result validateEmail(String token) {
        User user = User.findByEmail(ctx().session().get("email"), "global");

        if (token == null) {
            flash("danger", Messages.get("error.technical"));
            return badRequest(views.html.account.settings.emailValidate.render(user));
        }

        Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.email,"global");
        if (resetToken == null) {
            flash("danger", Messages.get("error.technical"));
            return badRequest(views.html.account.settings.emailValidate.render(user));
        }

        if (resetToken.isExpired()) {
            resetToken.delete();
            flash("danger", Messages.get("error.expiredmaillink"));
            return badRequest(views.html.account.settings.emailValidate.render(user));
        }

        user.email = resetToken.email;
        //user.changeEmail(resetToken.email,"global");
        user.save("global");


        session("email", resetToken.email);

        flash("success", Messages.get("account.settings.email.successful", user.email));
        resetToken.delete("global");
        return ok(views.html.account.settings.emailValidate.render(user));
        //return ok(views.html.dashboard.index.render(user));
    }
}
