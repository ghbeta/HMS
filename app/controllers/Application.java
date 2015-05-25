package controllers;

import models.User;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import play.*;
import play.api.libs.Crypto;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;

import views.html.*;


import java.io.File;
import java.io.IOException;

import static play.libs.Json.toJson;
public class Application extends Controller {
    public static User currentuser = null;
    public static Result index() {
        return ok(index.render("HMS"));
    }

    public static Result addUser(){

        User user = Form.form(User.class).bindFromRequest().get();
        user.sha1= Crypto.sign(user.matrikel);


        user.save();
        return ok(register.render());

        //return redirect(routes.Application.getUser());
    }

    public static Result getUser(){
        String matrikelnummer = DynamicForm.form().bindFromRequest().get("matrikel");
        User user = User.find.byId(matrikelnummer);
        if(user != null){
        currentuser = user;
        return ok(createrepo.render());}
        else
        {return ok("User is not found");}
    }

    public static Result addRepo(){

       File gitDir = new File("localrepo/"+currentuser.sha1+"/.git");
        try {
            FileRepository repo = new FileRepository(gitDir);
            repo.create();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok("leave it on purpose");
    }
}
