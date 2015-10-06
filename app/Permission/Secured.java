package Permission;

import models.User;
import models.UserRoll;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.forbidden;


public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        Logger.info(ctx.session().get("email"));
        User current=User.findByEmail(ctx.session().get("email"), "global");
        Logger.warn(current.roles);
        if(current.roles==null){
            current.roles="";
        }
       if(current.roles.equals(UserRoll.Defaultuser.toString())){
           Logger.warn("authorized user is allowed");
       return ctx.session().get("email");
       }
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
