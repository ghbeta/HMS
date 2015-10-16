package controllers.lectures.user;

import models.Lecture;
import models.Semesteruser;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.lectures.user.lecturehome;

/**
 * Created by Hao on 2015/10/15.
 */
public class Lecturehome extends Controller {
    public static Result generatelecturehome(String user, String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture selectedlecture=Lecture.getlecturebyname(lecture,semester);
        //todo somevalidate function should be placed here
        return ok(lecturehome.render(currentuser, currentsemesteruser, selectedlecture));

    }
}
