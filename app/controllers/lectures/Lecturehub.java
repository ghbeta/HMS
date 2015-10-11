package controllers.lectures;

import Permission.Securedstudents;
import controllers.Application;
import models.Lecture;
import models.Semester;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.signup.create;
import views.html.lectures.index;
import views.html.lectures.lecturemy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;

/**
 * Created by Hao on 2015/10/10.
 */

public class Lecturehub extends Controller {
    @Security.Authenticated(Securedstudents.class)
    public static Result allLectures() {
        User currentuser=User.findByEmail(request().username(),"global");
        List<Semester> semesters= Semester.getallsemester();
        if(semesters!=null){
            //TODO querying all the lectures using semester name
            flash("success",Messages.get("lecture.success"));
        return ok(index.render(currentuser, semesters));}
        else
        {
            flash("danger", Messages.get("lecture.danger"));
            return badRequest();
        }
    }
    @Security.Authenticated(Securedstudents.class)
    public static Result myLectures(){
        Map<String,List<Lecture>> lecturesacross= new HashMap<>();
        User currentuser=User.findByEmail(request().username(),"global");
        List<Semester> semesters= Semester.getallsemester();
        Logger.debug(semesters.get(1).semester);
        if(semesters != null){
           for(int i=0;i<semesters.size();i++){
               Logger.debug(semesters.get(i).semester);
               try {
                   lecturesacross.put(semesters.get(i).semester,Lecture.getalllectures(semesters.get(i).semester));
               } catch (Exception e) {
                   lecturesacross.put(semesters.get(i).semester, null);
               }
           }
           return ok(lecturemy.render(currentuser,lecturesacross));
        }
        else
        {
            flash("danger", Messages.get("lecture.danger"));
            return badRequest();
        }
    }
}
