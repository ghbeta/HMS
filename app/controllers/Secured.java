package controllers;

import models.User;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.forbidden;

/**
 * User: yesnault
 * Date: 22/01/12
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        Logger.info(ctx.session().get("password"));
        User current=User.findByEmail(ctx.session().get("email"), "global");
        Logger.warn(current.roles);
       if(current.roles=="user"){
       return ctx.session().get("email");}
        else
       {
           return null;
       }

//        return ctx.session().get("roles");

    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return ok(forbidden.render(User.findByEmail(ctx.session().get("email"), "global")));
    }
}
