package controllers;

import Utils.sha1generator;
import models.User;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;

import views.html.*;

import java.util.List;
import static play.libs.Json.toJson;
public class Application extends Controller {

    public static Result index() {
        return ok(index.render("HMS"));
    }

    public static Result addUser(){

        User user = Form.form(User.class).bindFromRequest().get();
        user.sha1= sha1generator.generateSha1(user.matrikel);
        user.save();
        return ok(register.render());
        //return redirect(routes.Application.getUser());
    }

    public static Result getUser(){
        String matrikelnummer = DynamicForm.form().bindFromRequest().get("matrikel");
        User user = User.find.byId(matrikelnummer);
        if(user != null){
        return ok(toJson(user));}
        else
        {return ok("User is not found");}
    }
}
