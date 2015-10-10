package controllers.account.settings;

import Permission.Secureddefault;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Index Settings page.
 *
 * User: yesnault
 * Date: 15/05/12
 */
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
