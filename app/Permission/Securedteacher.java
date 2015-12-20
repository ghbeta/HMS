package Permission;

import models.User;
import models.UserRoll;
import play.Logger;
import play.mvc.Http;
import play.mvc.Security;
import play.mvc.Result;
import views.html.forbidden;

/**
 * Created by Hao on 2015/10/11.
 */
public class Securedteacher extends Security.Authenticator{
    @Override
    public String getUsername(Http.Context ctx) {
            Logger.info(ctx.session().get("email"));
            User current=User.findByEmail(ctx.session().get("email"), "global");
            //Logger.debug("what is request in teacher:"+ctx.request().body().asText());
            //Logger.warn(current.roles);
        if(current!=null) {
            if (current.roles == null) {
                current.roles = "";
            }
            if (current.roles.equals(UserRoll.Teachers.toString())) {
                Logger.warn("authorized user is allowed");
                return ctx.session().get("email");
            } else {
                return null;
            }
        }else{
            return null;
        }

    //        return ctx.session().get("roles");

            }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        Logger.info(ctx.session().get("email"));
        User current=User.findByEmail(ctx.session().get("email"), "global");
            return ok(forbidden.render(current));

            }
}
