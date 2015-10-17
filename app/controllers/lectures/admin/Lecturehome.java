package controllers.lectures.admin;

import models.Lecture;
import models.Semester;
import models.Semesteruser;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.lectures.admin.lecturehome;

/**
 * Created by Hao on 2015/10/15.
 */
public class Lecturehome extends Controller {

    public static class Descriptionform{
        public String modifieddescription;
    }

    public static class Lecturetermform{

        public String coursename;


        public String localrepo;


        public String closingdate;


        public int totalassignment;


        public int optionalassigment;


        public int numberofvalidassignment;


        public float percentageforvalidassignment;


        public float percentageforexam;
    }
    public static Result generatelecturehome(String user, String semester,String lecture){
       User currentuser=User.findByEmail(ctx().session().get("email"),"global");
       Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
       Lecture selectedlecture=Lecture.getlecturebyname(lecture,semester);
        //todo somevalidate function should be placed here
        return ok(lecturehome.render(currentuser,currentsemesteruser,selectedlecture));

    }

    public static Result modifydescription(String user, String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Descriptionform> descriptionformForm=Form.form(Descriptionform.class).bindFromRequest();
        currentlecture.desription=descriptionformForm.get().modifieddescription;
        currentlecture.update(semester);
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
    }
}
