package controllers.account.settings;

import Permission.Secureddefault;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secureddefault.class)
public class Index extends Controller {

    /**
     * Main page settings
     *
     * @return index settings
     */
    public static Result index() {
        return Password.index();
    }
}
