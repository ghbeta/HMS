package controllers.lectures.admin;

import models.Lecture;
import models.Semester;
import models.Semesteruser;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.lectures.admin.lecturehome;

/**
 * Created by Hao on 2015/10/15.
 */
public class Lecturehome extends Controller {
    public static Result generatelecturehome(String user, String semester,String lecture){
       User currentuser=User.findByEmail(ctx().session().get("email"),"global");
       Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
       Lecture selectedlecture=Lecture.getlecturebyname(lecture,semester);
        return ok(lecturehome.render(currentuser,currentsemesteruser,selectedlecture));

    }
}
