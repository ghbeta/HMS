package controllers.lectures;

import Permission.Securedstudents;
import Permission.Securedteacher;
import controllers.Application;
import models.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.io.Zip;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.signup.create;
import views.html.lectures.filestatus;
import views.html.lectures.index;
import views.html.lectures.lecturemy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;
import static utils.RepoManager.reponame;
import static utils.UploadPath.localrepopullpath;

/**
 * Created by Hao on 2015/10/10.
 */

public class Lecturehub extends Controller {
    @Security.Authenticated(Securedstudents.class)
    public static Result allLectures() {
        Map<String,List<Lecture>> alllectures= new HashMap<>();
        User currentuser=User.findByEmail(request().username(),"global");
        List<Semester> semesters= Semester.getallsemester();
        if(semesters!=null){
            for(int i=0;i<semesters.size();i++){
                try {
                    alllectures.put(semesters.get(i).semester,Lecture.getalllectures(currentuser.email,semesters.get(i).semester));
                } catch (Exception e) {
                    alllectures.put(semesters.get(i).semester, null);
                }
            }
            flash("success",Messages.get("lecture.success"));
        return ok(index.render(currentuser, alllectures));}
        else
        {
            Logger.warn("semesters is null");
            flash("danger", Messages.get("lecture.danger"));
            return badRequest(index.render(currentuser, alllectures));
        }
    }
    @Security.Authenticated(Securedstudents.class)
    public static Result myLectures(){
        Map<String,List<Lecture>> mylecturesacross= new HashMap<>();
        User currentuser=User.findByEmail(request().username(),"global");
        List<Semester> semesters= Semester.getallsemester();
        if(semesters != null){
           for(int i=0;i<semesters.size();i++){
               try {
                   mylecturesacross.put(semesters.get(i).semester,Lecture.getalllecturesbyemail(currentuser.email,semesters.get(i).semester));
               } catch (Exception e) {
                   mylecturesacross.put(semesters.get(i).semester, null);
               }
           }
           return ok(lecturemy.render(currentuser,mylecturesacross));
        }
        else
        {
            flash("danger", Messages.get("lecture.danger"));
            return badRequest(lecturemy.render(currentuser,mylecturesacross));
        }
    }
    @Security.Authenticated(Securedstudents.class)
    public static Result getFiles(String semester,String lecturename,String assignmenttitle){
        try{
            Assignment mats=Assignment.findByLectureAndName(semester,lecturename,assignmenttitle);
            return ok(new File(mats.uploadfile));

        }catch (Exception e){
            flash("danger", Messages.get("file.notexist"));
            return badRequest(filestatus.render());
        }
    }

    @Security.Authenticated(Securedstudents.class)
    public static Result getFeedBackFiles(String semester,String lecturename,String assignmenttitle){
        try{
            User currentuser=User.findByEmail(ctx().session().get("email"),"global");
            Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
            Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
            String des=assignmenttitle+"/";
            String localrepopath=localrepopullpath(semester,lecturename,currentuser.id,reponame(lecture, semesteruser));
            String feedbackpath=localrepopath+"/"+des;
            Logger.debug("zip download path "+feedbackpath);
            ZipFile zipFile = new ZipFile(FileUtils.getTempDirectoryPath()+"/"+assignmenttitle+".zip");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFolder(feedbackpath,parameters);
            return ok(new File(FileUtils.getTempDirectoryPath()+"/"+assignmenttitle+".zip"));
        }
        catch (Exception e){
            flash("danger",Messages.get("file.notexist"));
            Logger.debug(e.getMessage());
            return badRequest(filestatus.render());
        }
    }
//    @Security.Authenticated(Securedteacher.class)
//    public static Result deleteFiles(String lecture,String assignment,String semester,String filename){
//        //Lecture currentlecture = Lecture.getlecturebyname(lecture);
//        Assignment currentassignment = Assignment.findByLectureAndName(semester,lecture,assignment);
//        try {
//            FileUtils.forceDelete(new File("files/"+assignment.uploadfile));
//            assignment.uploadfile=null;
//            assignment.filename=null;
//            assignment.update(semester);
//            flash("success",Messages.get("file.delete"));
//            return ok();
//        } catch (IOException e) {
//            flash("danger",Messages.get("file.delete.fail"));
//            return badRequest();
//        }
//
//
//    }
}
