package controllers.account.Permission;

import models.User;
import models.UserRoll;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.forbidden;

/**
 * Created by Hao on 2015/10/2.
 */
public class Permitall extends Security.Authenticator {
    @Override
    public String getUsername(Http.Context ctx) {
      if(ctx.session().get("email")!=null){
          return ctx.session().get("email");
      }

      else {
          return "";//ctx.session().get("email");
      }
    }

}
