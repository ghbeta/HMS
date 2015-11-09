package controllers.system;

import Permission.Securedadmin;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.admin.usermanagement;


/**
 * Created by Hao on 2015/11/9.
 */
public class SystemControll extends Controller {

    @Security.Authenticated(Securedadmin.class)
    public static Result usermanagement(){

        return ok(usermanagement.render(User.findByEmail(request().username(), "global"),null));
    }



}
