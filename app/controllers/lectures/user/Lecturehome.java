package controllers.lectures.user;

import Permission.Securedstudents;
import controllers.lectures.*;
import models.Lecture;
import models.Semesteruser;
import models.User;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.lectures.user.lecturehome;

/**
 * Created by Hao on 2015/10/15.
 */
public class Lecturehome extends Controller {
    @Security.Authenticated(Securedstudents.class)
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
    @Security.Authenticated(Securedstudents.class)
    public static Result addSemesterusertoLecture(String user, String semester,String lecturename){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        if(Lecture.addSemesteruser(semester,semesteruser,lecture)){
            semesteruser.assignments.addAll(lecture.assignments);
            semesteruser.update(semester);
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }
        else
        {
            flash("danger", Messages.get("lecture.adduser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
        }
    }

    @Security.Authenticated(Securedstudents.class)
    public static Result deleteSemesteruserfromlecture(String user, String semester,String lecturename){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        if(Lecture.deleteSemesteruser(semester,semesteruser,lecture)){
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }
        else
        {
            flash("danger", Messages.get("lecture.adduser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
        }
    }
}
