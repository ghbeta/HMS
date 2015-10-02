package controllers;

import controllers.account.Permission.Secured;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;



public class Dashboard extends Controller {
    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render(User.findByEmail(request().username(),"global")));
    }
}
