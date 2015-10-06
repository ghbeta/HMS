package controllers;

import Permission.Permitall;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.about;
import play.Logger;
/**
 * Created by Hao on 2015/10/1.
 */
public class About extends Controller {
    @Security.Authenticated(Permitall.class)
    public static Result index() {
        Logger.info(request().username());
        return ok(about.render(User.findByEmail(request().username(),"global")));
    }
}
