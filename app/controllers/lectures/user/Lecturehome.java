package controllers.lectures.user;

import Permission.Securedstudents;
import com.jcraft.jsch.Session;
import models.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CreateRepo;
import views.html.lectures.user.lecturehome;

import java.io.IOException;
import java.util.Collection;

import static utils.CreateRepo.hostparser;

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
            try{
                CreateRepo.deleteRepo(currentuser,lecture,request().getHeader("Host"));
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
            }catch (Exception e){
                flash("danger", Messages.get("lecture.adduser.fail"));
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
            }

        }
        else
        {
            flash("danger", Messages.get("lecture.adduser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
        }
    }

    public static String lastUpdateStatus(Semesteruser semesteruser,Lecture lecture){
        String serverhost=request().getHeader("Host");
        String reponame=lecture.courseName+"_"+"a6d14de05d7b2c3cf4fae7ae14cfa7f3";//semesteruser.userHash;
        String gitpath= "git@"+ hostparser(serverhost)+":"+reponame+".git";
        String result="";
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
       try{
        Collection<Ref> refs = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(gitpath).call();
           for (Ref ref : refs) {
               System.out.println("Ref: " + ref);
               result.concat(ref.toString());
           }
       return result;
           }
       catch(Exception e){
           return e.getMessage();
       }

    }

}
