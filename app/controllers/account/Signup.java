package controllers.account;

import controllers.Application;
import models.Semesteruser;
import models.User;
import models.UserRoll;
import utils.AppException;
import utils.CreateExternalId;
import utils.Hash;
import utils.Mail;
import org.apache.commons.mail.EmailException;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.account.signup.create;
import views.html.index;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import static play.data.Form.form;

public class Signup extends Controller {

    /**
     * Display the create form.
     *
     * @return create form
     */
    public static Result create() {
        return ok(create.render(form(Application.Register.class)));
    }

    /**
     * Display the create form only (for the index page).
     *
     * @return create form
     */
    public static Result createFormOnly() {
        return ok(create.render(form(Application.Register.class)));
    }

    /**
     * Save the new user.
     *
     * @return Successfull page or created form if bad
     */
    public static Result save() {
        Form<Application.Register> registerForm = form(Application.Register.class).bindFromRequest();

        if (registerForm.hasErrors()) {
            return badRequest(views.html.index.render());
        }

        Application.Register register = registerForm.get();
        Result resultError = checkBeforeSave(registerForm, register.email,register.id);

        if (resultError != null) {
            return resultError;
        }

        try {
            User user = new User();
            if(register.id==null||register.id.isEmpty()){
                user.id= CreateExternalId.generateId();
                user.roles=UserRoll.Defaultuser.toString();
            }
            else{
            user.id=register.id;
                user.roles=UserRoll.Students.toString();}
            user.email = register.email;
            user.lastname = register.lastname;
            user.firstname = register.firstname;
            user.passwordHash = Hash.createPassword(register.inputPassword);
            user.confirmationToken = UUID.randomUUID().toString();
            user.setUserHash();

            user.save("global");
            sendMailAskForConfirmation(user);
            flash("success",Messages.get("signup.msg.created"));
            //return ok(views.html.account.signup.created.render(true));
            return ok(views.html.index.render());
        } catch (EmailException e) {
            Logger.debug("Signup.save Cannot send email", e);
            flash("danger", Messages.get("error.sending.email"));
        } catch (Exception e) {
            Logger.error("Signup.save error", e);
            flash("danger", Messages.get("error.technical"));
        }
        return badRequest(views.html.index.render());
    }

    /**
     * Check if the email already exists.
     *
     * @param registerForm User Form submitted
     * @param email email address
     * @return Index if there was a problem, null otherwise
     */
    private static Result checkBeforeSave(Form<Application.Register> registerForm, String email,String id) {
        // Check unique email
        if (User.findByEmail(email,"global") != null||User.findById(id,"global")!=null) {
            flash("danger", Messages.get("error.email.already.exist"));
            return badRequest(views.html.index.render());
        }

        return null;
    }

    /**
     * Send the welcome Email with the link to confirm.
     *
     * @param user user created
     * @throws EmailException Exception when sending mail
     */
    private static void sendMailAskForConfirmation(User user) throws EmailException, MalformedURLException {
        String subject = Messages.get("mail.confirm.subject");
        String urlString = "http://" + Configuration.root().getString("server.hostname");
        urlString += "/confirm/" + user.confirmationToken;
        URL url = new URL(urlString); // validate the URL, will throw an exception if bad.
        String message = Messages.get("mail.confirm.message", url.toString());

        Mail.Envelop envelop = new Mail.Envelop(subject, message, user.email);
        Mail.sendMail(envelop);
    }


    /**
     * Valid an account with the url in the confirm mail.
     *
     * @param token a token attached to the user we're confirming.
     * @return Confirmationpage
     */
    public static Result confirm(String token) {
        User user = User.findByConfirmationToken(token,"global");
        if (user == null) {
            flash("danger", Messages.get("error.unknown.email"));
            return badRequest(views.html.account.signup.confirm.render(null));
        }

        if (user.validated) {
            flash("danger", Messages.get("error.account.already.validated"));
            return badRequest(views.html.account.signup.confirm.render(null));
        }

        try {
            if (User.confirm(user,"global")) {
                sendMailConfirmation(user);
                flash("success", Messages.get("account.successfully.validated"));
                user.dateCreation=new Date();
                //user.roles= UserRoll.Defaultuser.toString();
                user.save("global");
                return ok(views.html.account.signup.confirm.render(user));
            } else {
                Logger.debug("Signup.confirm cannot confirm user");
                flash("danger", Messages.get("error.confirm"));
                return badRequest(views.html.account.signup.confirm.render(null));
            }
        } catch (AppException e) {
            Logger.error("Cannot signup", e);
            flash("danger", Messages.get("error.technical"));
        } catch (EmailException e) {
            Logger.debug("Cannot send email", e);
            flash("danger", Messages.get("error.sending.confirm.email"));
        }
        return badRequest(views.html.account.signup.confirm.render(null));
    }

    /**
     * Send the confirm mail.
     *
     * @param user user created
     * @throws EmailException Exception when sending mail
     */
    private static void sendMailConfirmation(User user) throws EmailException {
        String subject = Messages.get("mail.welcome.subject");
        String message = Messages.get("mail.welcome.message");
        Mail.Envelop envelop = new Mail.Envelop(subject, message, user.email);
        Mail.sendMail(envelop);
    }
}
