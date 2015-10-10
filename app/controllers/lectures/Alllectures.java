package controllers.lectures;

import Permission.Securedstudents;
import controllers.Application;
import models.Semester;
import models.User;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.signup.create;
import views.html.lectures.index;

import java.util.List;

import static play.data.Form.form;

/**
 * Created by Hao on 2015/10/10.
 */

public class Alllectures extends Controller {
    @Security.Authenticated(Securedstudents.class)
    public static Result allLectures() {
        User currentuser=User.findByEmail(request().username(),"global");
        List<Semester> semesters= Semester.getallsemester();
        if(semesters!=null){
            flash("success",Messages.get("lecture.success"));
        return ok(index.render(currentuser, semesters));}
        else
        {
            flash("danger", Messages.get("lecture.danger"));
            return badRequest();
        }
    }
}
