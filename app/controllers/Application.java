package controllers;

import models.User;
import models.UserRoll;
import utils.AppException;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.Date;

import static play.data.Form.form;

/**
 * Login and Logout.
 * User: yesnault
 */
public class Application extends Controller {

    public static Result GO_HOME = redirect(
            controllers.routes.Application.index()



    );

    public static Result GO_DASHBOARD = redirect(
            controllers.routes.Dashboard.index()
    );

    public static Result GO_ADMIN = redirect(
           controllers.routes.Dashboard.admindashboard()
    );

    /**
     * Display the login page or dashboard if connected
     *
     * @return login page or dashboard
     */
    public static Result index() {
        // Check that the email matches a confirmed user before we redirect
        String email = ctx().session().get("email");
        if (email != null) {
            User user = User.findByEmail(email,"global");
            if (user != null && user.validated) {
                return GO_DASHBOARD;
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }

        return ok(index.render());
    }

    /**
     * Login class used by Login Form.
     */
    public static class Login {

        @Constraints.Required
        public String email;
        @Constraints.Required
        public String password;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            User user = null;
            try {
                user = User.authenticate(email, password,"global");
            } catch (AppException e) {
                return Messages.get("error.technical");
            }
            if (user == null) {
                return Messages.get("invalid.user.or.password");
            } else if (!user.validated) {
                return Messages.get("account.not.validated.check.mail");
            }
            return null;
        }

    }

    public static class Register {
        public String id;

        @Constraints.Required
        public String email;

        @Constraints.Required
        public String lastname;

        @Constraints.Required
        public String firstname;

        @Constraints.Required
        public String inputPassword;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(email)) {
                return "Email is required";
            }

            if (isBlank(lastname)) {
                return "lastname is required";
            }

            if (isBlank(firstname)) {
                return "firstname is required";
            }

            if (isBlank(inputPassword)) {
                return "Password is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }

    /**
     * Handle login form submission.
     *
     * @return Dashboard if auth OK or login form if auth KO
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();

        //Form<Register> registerForm = form(Register.class);

        if (loginForm.hasErrors()) {
            //mod
            return badRequest(index.render());
        } else {
            session("email", loginForm.get().email);
            User user = User.findByEmail(loginForm.get().email,"global");
            if(user!=null){
                user.dateLastlogin=new Date();
                user.save("global");
            }
            if(user.roles.equals(UserRoll.SystemAdmin.toString())){
                return GO_ADMIN;
            }else{
            return GO_DASHBOARD;}
        }
    }

   public static Result directlogin(User user){
       Logger.info("direkt login"+user.email);
       session("email",user.email);
       return GO_DASHBOARD;
   }

    /**
     * Logout and clean the session.
     *
     * @return Index page
     */
    public static Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return GO_HOME;
    }



}