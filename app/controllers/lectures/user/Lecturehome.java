package controllers.lectures.user;

import controllers.lectures.*;
import models.Lecture;
import models.Semesteruser;
import models.User;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.lectures.user.lecturehome;

/**
 * Created by Hao on 2015/10/15.
 */
public class Lecturehome extends Controller {
    public static Result generatelecturehome(String user, String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Lecture selectedlecture=Lecture.getlecturebyname(lecture,semester);
        Semesteruser currentsemesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);


        if(selectedlecture.isExpired()){
        return ok(lecturehome.render(currentuser, currentsemesteruser, selectedlecture));}
        else
        {
            flash("danger", Messages.get("lecture.home.expired"));
            return redirect(controllers.lectures.routes.Lecturehub.myLectures());
        }

    }
}
