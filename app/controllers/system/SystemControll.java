package controllers.system;

import Permission.Securedadmin;
import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.admin.usermanagement;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hao on 2015/11/9.
 */
public class SystemControll extends Controller {

    @Security.Authenticated(Securedadmin.class)
    public static Result usermanagement(){
        List<User> userlist=new ArrayList<>();
        DynamicForm requestData = Form.form().bindFromRequest();
        String id=requestData.get("id");
        String lastname=requestData.get("lastname");
        String firstname=requestData.get("firstname");
        if(id!=null){
            userlist.add(User.findById(id,"global"));
        }
        if(id == null&&lastname!=null&&firstname!=null){

            userlist=User.findByName(lastname,firstname,"global");
        }
        else{
            userlist=User.findAll("global");
        }


        return ok(usermanagement.render(User.findByEmail(request().username(), "global"),userlist));
    }

    @Security.Authenticated(Securedadmin.class)
   public static Result usermanagementinit(){
       return ok(usermanagement.render(User.findByEmail(request().username(), "global"),null));
   }

    @BodyParser.Of(BodyParser.Json.class)
    @Security.Authenticated(Securedadmin.class)
    public static Result changeuserrole(){
        JsonNode json = request().body().asJson();
        String email=json.findPath("email").asText();
        String role=json.findPath("role").asText();
        if(email!=null&&role!=null){
            User user=User.findByEmail(email,"global");
            user.roles=role;
            user.update("global");
            return ok(Messages.get("change.role.success"));
        }
       return badRequest(Messages.get("change.role.fail"));
    }

}
