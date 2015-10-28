package controllers.lectures.user;

import Permission.Securedstudents;
import models.*;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CreateRepo;
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
    public static Result addRemoteRepotoLecture(String user1,String semester,String lecturename){
        Semesteruser semesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
        User user=User.findByEmail(ctx().session().get("email"),"global");
        try{


        //System.out.println(request().getHeader("Host")+System.getProperty("user.home"));
       String repopath=CreateRepo.createRemoteRepo(user,lecture,request().getHeader("Host"));
       if(repopath!=null){

           Repo newrepo = new Repo();
           newrepo.course=lecture;
           newrepo.owner=semesteruser;
           newrepo.repopath=repopath;
           newrepo.semester=lecture.semester;
           newrepo.save(lecture.semester);
           //semesteruser.repos.add(newrepo);
           //semesteruser.update(lecture.semester);
        return  redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,lecture.semester,lecture.courseName));}
            else{
           flash("danger",Messages.get("repo.create.after.fail"));
           return  redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,lecture.semester,lecture.courseName));
       }


        }
        catch(Exception e){
            flash("danger",e.getMessage());
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }
    }
    @Security.Authenticated(Securedstudents.class)
    public static Result addSemesterusertoLecture(String user, String semester,String lecturename){
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        if(Lecture.addSemesterusertoLecture(semester, semesteruser, lecture)){
            if(semesteruser.roles.equals(UserRoll.Students.toString())){
            semesteruser.assignments.addAll(lecture.assignments);
            try{
                String repopath=CreateRepo.createRemoteRepo(currentuser,lecture,request().getHeader("Host"));
                if(repopath!=null){
                    Repo newrepo = new Repo();
                    newrepo.course=lecture;
                    newrepo.owner=semesteruser;
                    newrepo.repopath=repopath;
                    newrepo.semester=lecture.semester;
                    newrepo.save(lecture.semester);
                    //semesteruser.repos.add(newrepo);
                    //semesteruser.update(lecture.semester);
                    //semesteruser.update(semester);
                    return  redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,lecture.semester,lecture.courseName));}
                else{
                    flash("danger",Messages.get("repo.create.after.fail"));
                    return  redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,lecture.semester,lecture.courseName));
                }


            }catch(Exception e){
                flash("danger",Messages.get("Error.add.user.lecture"));
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
            }

            }
            else{
            semesteruser.update(semester);
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));}
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
        if(Lecture.deleteSemesteruserfromLecture(semester, semesteruser, lecture)){
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
        }
        else
        {
            flash("danger", Messages.get("lecture.adduser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
        }
    }


}
