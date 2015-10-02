package controllers.account.settings;

import controllers.account.Permission.Secured;
import models.Token;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.net.MalformedURLException;

/**
 * User: yesnault
 * Date: 15/05/12
 */
@Security.Authenticated(Secured.class)
public class Password extends Controller {

    /**
     * Password Page. Ask the user to change his password.
     *
     * @return index settings
     */
    public static Result index() {
        Logger.debug(User.findByEmail(request().username(),"global").firstname);
        return ok(views.html.account.settings.password.render(User.findByEmail(request().username(),"global")));
    }

    /**
     * Send a mail with the reset link.
     *
     * @return password page with flash error or success
     */
    public static Result runPassword() {
        User user = User.findByEmail(request().username(),"global");
        try {
            Token.sendMailResetPassword(user,"global");
            flash("success", Messages.get("resetpassword.mailsent"));
            return ok(views.html.account.settings.password.render(user));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(views.html.account.settings.password.render(user));
    }
}
