package controllers.lectures.admin;

import Permission.Securedassistant;
import Permission.Securedteacher;
import controllers.Assets;
import models.*;
import play.Logger;
import play.Play;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.*;
import views.html.lectures.admin.lecturehome;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import javax.persistence.Column;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import static utils.UploadPath.uploadpath;
/**
 * Created by Hao on 2015/10/15.
 */
public class Lecturehome extends Controller {

    public static class Descriptionform{
        public String modifieddescription;
    }

    public static class Lecturetermform{


        public String localrepo;


        public Date closingdate;


        public int totalassignment;


        public int optionalassigment;


        public int numberofvalidassignment;


        public float percentageforvalidassignment;


        public float percentageforexam;
    }

    public static class Assignmentform{

        public int numberofexercise;

        public File uploadfile;

        @Column(columnDefinition = "TEXT")
        public String addtionalinfo;

        public Date deadline;


    }

    @Security.Authenticated(Securedteacher.class)
    public static Result generatelecturehome(String user, String semester,String lecture){
       User currentuser=User.findByEmail(ctx().session().get("email"),"global");
       Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
       Lecture selectedlecture=Lecture.getlecturebyname(lecture,semester);
        //todo somevalidate function should be placed here
        return ok(lecturehome.render(currentuser,currentsemesteruser,selectedlecture));

    }

    @Security.Authenticated(Securedteacher.class)
    public static Result modifydescription(String user, String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Descriptionform> descriptionformForm=Form.form(Descriptionform.class).bindFromRequest();

        currentlecture.desription=descriptionformForm.get().modifieddescription;
        currentlecture.update(semester);
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result modifyterms(String user, String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Lecturetermform> lecturetermformForm=Form.form(Lecturetermform.class).bindFromRequest();
        //currentlecture.desription=descriptionformForm.get().modifieddescription;
        currentlecture.localrepo=lecturetermformForm.get().localrepo.equals("true");
        currentlecture.closingdate=lecturetermformForm.get().closingdate;
        currentlecture.totalassignment=lecturetermformForm.get().totalassignment;
        currentlecture.optionalassignments=lecturetermformForm.get().optionalassigment;
        currentlecture.requriednumberofvalidassignment=lecturetermformForm.get().numberofvalidassignment;
        currentlecture.requiredpercentfovalidassignment=lecturetermformForm.get().percentageforvalidassignment;
        currentlecture.minimumPercentageForExamination=lecturetermformForm.get().percentageforexam;
        currentlecture.update(semester);
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result addassignment(String user,String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Assignmentform> assignmentformForm=Form.form(Assignmentform.class).bindFromRequest();


        Assignment assignment=new Assignment();
        assignment.numberofexercise=assignmentformForm.get().numberofexercise;
        if(currentlecture.assignments.size()==0){
            assignment.title= Messages.get("lecture.homework")+1;

        }else{
            if(currentlecture.assignments.size()+1<=currentlecture.totalassignment){
            assignment.title=Messages.get("lecture.homework")+(currentlecture.assignments.size()+1);}
            else if(currentlecture.assignments.size()+1>currentlecture.totalassignment&&currentlecture.assignments.size()+1<=currentlecture.totalassignment+currentlecture.optionalassignments){
                assignment.title=Messages.get("lecture.homework.optional")+(currentlecture.assignments.size()+1);
            }

        }
        assignment.addtionalinfo=assignmentformForm.get().addtionalinfo;
        assignment.deadline=assignmentformForm.get().deadline;
        MultipartFormData body= request().body().asMultipartFormData();
        FilePart filePart=body.getFile("uploadfile");
        String filename="";
        if(filePart != null){
            filename = filePart.getFilename();
            //String contentType=filePart.getContentType();
            Logger.debug("what is:"+filename);

            File file= filePart.getFile();
            String path="";
            try {
                FileUtils.moveFile(file, new File("~/"+uploadpath("assignment",semester,lecture), filename));
                path = uploadpath("assignment", semester, lecture)+filename;
            } catch (IOException ioe) {
                flash("danger",Messages.get("Lecture.assignment.uploadfail"));
                return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
            }



            assignment.uploadfile=path;
            assignment.filename=filename;
            currentlecture.assignments.add(assignment);
            currentlecture.update(semester);
            flash("success", Messages.get("Lecture.assignment.create"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        } else {
            flash("danger",Messages.get("Lecture.assignment.uploadfail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }



    }

    @Security.Authenticated(Securedteacher.class)
    public static Result modifyassignment(String assignmentid,String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Assignmentform> assignmentformForm=Form.form(Assignmentform.class).bindFromRequest();
        Assignment assignment=Assignment.findById(semester,assignmentid);
        assignment.numberofexercise=assignmentformForm.get().numberofexercise;
        assignment.addtionalinfo=assignmentformForm.get().addtionalinfo;
        assignment.deadline=assignmentformForm.get().deadline;
        assignment.update(semester);
        flash("success", Messages.get("Lecture.assignment.create"));
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
    }
}
