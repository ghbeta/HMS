package controllers.lectures.admin;

import Permission.Securedassistant;
import Permission.Securedteacher;
import Permission.Securedteacherorassistance;
import controllers.Assets;
import models.*;
import play.Logger;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.*;
import utils.DateConverter;
import views.html.lectures.admin.lecturehome;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import javax.persistence.Column;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
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


        public String closingdate;


        public int totalassignment;


        public int optionalassigment;


        public int numberofvalidassignment;


        public float percentageforvalidassignment;


        public float percentageforexam;
    }

    public static class Assignmentform{

        //public int numberofexercise;

        public File uploadfile;

        @Constraints.Required
        public float totalpoints;

        @Column(columnDefinition = "TEXT")
        public String addtionalinfo;

        public String deadline;

        public String isoptional;

    }

    @Security.Authenticated(Securedteacherorassistance.class)
    public static Result generatelecturehome(String user, String semester,String lecture){
       User currentuser=User.findByEmail(ctx().session().get("email"),"global");
       Semesteruser currentsemesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
       Lecture selectedlecture=Lecture.getlecturebyname(lecture,semester);
        if(selectedlecture.isExpired()){
            return ok(lecturehome.render(currentuser, currentsemesteruser, selectedlecture,null,null));}
        else
        {
            flash("danger", Messages.get("lecture.home.expired"));
            return redirect(controllers.lectures.routes.Lecturehub.myLectures());
        }

    }

    @Security.Authenticated(Securedteacherorassistance.class)
    public static Result addSemesterusertoLecture(String user,String semester,String lecturename){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        if(Lecture.addSemesterusertoLecture(semester, semesteruser, lecture)){
            semesteruser.update(semester);
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }else{
            flash("danger",Messages.get("lecture.adduser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }
    }

    @Security.Authenticated(Securedteacherorassistance.class)
    public static Result deleteSemesteruserfromlecture(String user,String semester,String lecturename){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        if(Lecture.deleteSemesteruserfromLecture(semester,semesteruser,lecture)){
            semesteruser.update(semester);
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }else{
            flash("danger", Messages.get("lecture.deleteuser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }
    }


    @Security.Authenticated(Securedteacher.class)
    public static Result modifydescription(String user, String semester,String lecture){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.getSemesteruserfomrUser(semester, currentuser);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Descriptionform> descriptionformForm=Form.form(Descriptionform.class).bindFromRequest();

        currentlecture.desription=descriptionformForm.get().modifieddescription;
        currentlecture.update(semester);
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result modifyterms(String user, String semester,String lecture) throws ParseException {
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.getSemesteruserfomrUser(semester, currentuser);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Lecturetermform> lecturetermformForm=Form.form(Lecturetermform.class).bindFromRequest();
        //currentlecture.desription=descriptionformForm.get().modifieddescription;
        currentlecture.localrepo=lecturetermformForm.get().localrepo.equals("true");
        currentlecture.closingdate=DateConverter.fromString(lecturetermformForm.get().closingdate);
        currentlecture.totalassignment=lecturetermformForm.get().totalassignment;
        currentlecture.optionalassignments=lecturetermformForm.get().optionalassigment;
        currentlecture.requriednumberofvalidassignment=lecturetermformForm.get().numberofvalidassignment;
        currentlecture.requiredpercentfovalidassignment=lecturetermformForm.get().percentageforvalidassignment;
        currentlecture.minimumPercentageForExamination=lecturetermformForm.get().percentageforexam;
        currentlecture.update(semester);
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result addassignment(String user,String semester,String lecture) throws ParseException {
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.getSemesteruserfomrUser(semester, currentuser);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Assignmentform> assignmentformForm=Form.form(Assignmentform.class).bindFromRequest();

        if(!assignmentformForm.hasErrors()){
        Assignment assignment=new Assignment();
        //assignment.numberofexercise=assignmentformForm.get().numberofexercise;
        if(assignmentformForm.get().isoptional==null){
            assignment.isoptional=false;
            assignment.title=Messages.get("lecture.homework")+(currentlecture.assignments.size()+1);
        }
        else{assignment.isoptional=true;
            assignment.title=Messages.get("lecture.homework.optional")+(currentlecture.assignments.size()+1);}
//        if(currentlecture.assignments.size()==0){
//            assignment.title= Messages.get("lecture.homework")+1;
//
//        }else{
//
//            if(currentlecture.assignments.size()+1<=currentlecture.totalassignment){
//            assignment.title=Messages.get("lecture.homework")+(currentlecture.assignments.size()+1);}
//            else if(currentlecture.assignments.size()+1>currentlecture.totalassignment&&currentlecture.assignments.size()+1<=currentlecture.totalassignment+currentlecture.optionalassignments){
//                assignment.title=Messages.get("lecture.homework.optional")+(currentlecture.assignments.size()+1);
//                assignment.isoptional=true;
//            }
//
//        }
        assignment.totalpoints=assignmentformForm.get().totalpoints;
        assignment.addtionalinfo=assignmentformForm.get().addtionalinfo;
        assignment.deadline= DateConverter.fromString(assignmentformForm.get().deadline);
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
                //file.renameTo(new File("~/"+uploadpath("assignment",semester,lecture)));
                File precheck=new File(uploadpath("assignment",semester,lecture), filename);
                if(precheck.exists()){
                    precheck.delete();
                }
                FileUtils.moveFile(file, new File(uploadpath("assignment",semester,lecture), filename));
                path = uploadpath("assignment", semester, lecture)+"/"+filename;
                Logger.debug("upload path "+path);
                assignment.uploadfile=path;
                assignment.filename=filename;
                assignment.semester=semester;
                currentlecture.assignments.add(assignment);
                currentlecture.update(semester);
                flash("success", Messages.get("Lecture.assignment.create"));
                return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
            } catch (Exception ioe) {
                flash("danger",Messages.get("Lecture.assignment.uploadfail"));
                return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
            }




        }
        else {
            flash("danger",Messages.get("Lecture.assignment.uploadfail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }

        } else {
            flash("danger",Messages.get("Lecture.assignment.uploadfail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }



    }

    @Security.Authenticated(Securedteacher.class)
    public static Result modifyassignment(String assignmentid,String semester,String lecture) throws ParseException {
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentsemesteruser=Semesteruser.getSemesteruserfomrUser(semester, currentuser);
        Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);
        Form<Assignmentform> assignmentformForm=Form.form(Assignmentform.class).bindFromRequest();
        if(!assignmentformForm.hasErrors()){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        //assignment.numberofexercise=assignmentformForm.get().numberofexercise;
        assignment.addtionalinfo=assignmentformForm.get().addtionalinfo;
        Logger.debug("date picker time string is "+assignmentformForm.get().deadline);
        assignment.deadline=DateConverter.fromString(assignmentformForm.get().deadline);
        assignment.totalpoints=assignmentformForm.get().totalpoints;
        FilePart filePart=null;
       try{
        MultipartFormData body= request().body().asMultipartFormData();
        filePart=body.getFile("uploadfile");}
       catch(Exception e){
           filePart=null;
       }
        String filename="";


        //String contentType=filePart.getContentType();
        //Logger.debug("what is:"+filename);


        String path="";
        if(filePart != null){
        try {
            File file= filePart.getFile();
            filename = filePart.getFilename();
            FileUtils.forceDelete(new File("files/"+assignment.uploadfile));
            FileUtils.moveFile(file, new File("files/"+uploadpath("assignment",semester,lecture), filename));
            path = uploadpath("assignment", semester, lecture)+"/"+filename;
            assignment.uploadfile=path;
            assignment.filename=filename;
            assignment.update(semester);
            flash("success",Messages.get("file.delete"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        } catch (IOException e) {
            flash("danger",Messages.get("file.delete.fail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }
        }
        else {
            assignment.update(semester);
            flash("success", Messages.get("Lecture.assignment.create"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }}
        else{
            Logger.warn("modify assignment form has errors");
            flash("danger", Messages.get("Lecture.assignment.modify.fail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }
    }
    @Security.Authenticated(Securedteacher.class)
    public static Result deleteAssignment(String lecture,String assignment,String semester){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");

        try{
            Lecture currentlecture=Lecture.getlecturebyname(lecture,semester);

            Assignment currentassignment = Assignment.findByLectureAndName(semester,lecture,assignment);
            if(currentassignment.handins.size()==0){
            Assignment.deleteAssignment(semester,currentassignment);
            flash("success", Messages.get("assignment.delete"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));}
            else{
                flash("danger",Messages.get("assignment.delete.fail"));
                return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,lecture));
            }
        }catch (Exception e){
            flash("danger",Messages.get("assignment.delete.fail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,lecture));
        }

    }
}
