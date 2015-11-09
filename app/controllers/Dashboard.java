package controllers;

import Permission.Securedadmin;
import Permission.Secureddefault;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.dashboardadmin;
import views.html.dashboard.index;



public class Dashboard extends Controller {
    @Security.Authenticated(Secureddefault.class)
    public static Result index() {
        return ok(index.render(User.findByEmail(request().username(),"global")));
    }

    @Security.Authenticated(Securedadmin.class)
    public static Result admindashboard(){return ok(dashboardadmin.render(User.findByEmail(request().username(),"global")));}
}
