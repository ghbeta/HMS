package controllers.lectures.user;

import Permission.Securedstudents;
import com.jcraft.jsch.Session;
import models.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.RepoManager;
import views.html.lectures.user.lecturehome;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import java.io.File;
import java.util.Date;

import static utils.RepoManager.hostparser;
import static utils.RepoManager.reponame;
import static utils.RepoManager.userrepofilepath;
import static utils.UploadPath.uploadpath;

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


        Logger.debug("Generate RemoteRepo afterwards:" + request().getHeader("Host") + System.getProperty("user.home"));
       String repopath= RepoManager.createRemoteRepo(user, lecture, request().getHeader("Host"));
       if(repopath!=null){

           Repo newrepo = new Repo();
           newrepo.course=lecture;
           newrepo.owner=semesteruser;
           newrepo.repopath=repopath;
           newrepo.semester=lecture.semester;
           newrepo.setRepofilepath(reponame(lecture,semesteruser));
           newrepo.save(lecture.semester);
           //semesteruser.repos.add(newrepo);
           //semesteruser.update(lecture.semester);
        return  redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,lecture.semester,lecture.courseName));}
            else{
           flash("danger",Messages.get("repo.create.after.fail.nossh"));
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
                if(!semesteruser.assignments.containsAll(lecture.assignments)){
            semesteruser.assignments.addAll(lecture.assignments);}
                semesteruser.update(semester);
            try{
                String repopath= RepoManager.createRemoteRepo(currentuser, lecture, request().getHeader("Host"));
                if(repopath!=null){
                    Repo newrepo = new Repo();
                    newrepo.course=lecture;
                    newrepo.owner=semesteruser;
                    newrepo.repopath=repopath;
                    newrepo.semester=lecture.semester;
                    newrepo.setRepofilepath(reponame(lecture,semesteruser));
                    newrepo.save(lecture.semester);
                    //semesteruser.repos.add(newrepo);
                    //semesteruser.update(lecture.semester);
                    //semesteruser.update(semester);
                    return  redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,lecture.semester,lecture.courseName));}
                else{
                    flash("danger",Messages.get("repo.create.after.fail.nossh"));
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
                RepoManager.deleteRepo(currentuser, lecture, request().getHeader("Host"));
                semesteruser.assignments.removeAll(lecture.assignments);
                semesteruser.update(semester);//todo test here
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
            }catch (Exception e){
                flash("danger", Messages.get("lecture.deleteuser.fail"));
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
            }

        }
        else
        {
            flash("danger", Messages.get("lecture.deleteuser.fail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
        }
    }

    public static String lastUpdateStatus(Semesteruser semesteruser,Lecture lecture){
        String serverhost=request().getHeader("Host");
        String reponame=lecture.courseName+"_"+semesteruser.userHash;
        String gitpath= "git@"+ hostparser(serverhost)+":"+reponame+".git";
        String result="";
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
       try{

           System.out.println(userrepofilepath(reponame));
           Repository repository=new FileRepository(userrepofilepath(reponame));
           Ref head = repository.getRef("refs/heads/master");

           try (RevWalk walk = new RevWalk(repository)) {
               RevCommit commit = walk.parseCommit(head.getObjectId());

               System.out.println("Commit-Message: " + commit.getFullMessage());

               walk.dispose();
      if(commit.getFullMessage().isEmpty()||commit.getFullMessage()==null){
          return Messages.get("Localrepo.status.none");
      }
               else{

       return "Commit-Message: " + commit.getFullMessage();}
           }}
       catch(Exception e){
           return e.getMessage();
       }

    }

    public static Result handinhomework(String assignmentid,String user,String semester,String lecturename){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        DynamicForm handinform = Form.form().bindFromRequest();
        String commit="";
        if(handinform.get("commit")==null||handinform.get("commit").isEmpty()||handinform.get("commit").equals("")){
            commit  =Messages.get("lecture.uploadsolution")+assignment.title;
        }else{
            commit = handinform.get("commit");
        }
        Logger.warn("commit message is "+commit);
        try{
            MultipartFormData body = request().body().asMultipartFormData();
            FilePart homeworkfile = body.getFile("homeworkfile");
            if (homeworkfile != null) {
                String fileName = homeworkfile.getFilename();
                Logger.debug("uploaded homework:"+fileName);
                File file = homeworkfile.getFile();

                Repo repo=Repo.findRepoByLectureAndOwner(assignment.semester,semesteruser,assignment.lecture);
                File localPath = File.createTempFile(reponame(assignment.lecture, semesteruser), "");
                localPath.delete();


                Logger.debug("Cloning from "+repo.repofilepath+"to"+localPath);
                Git git = Git.cloneRepository()
                        .setURI(repo.repofilepath)
                        .setDirectory(localPath)
                        .call();
                Logger.debug("create local repo: "+git.getRepository().getDirectory());
                File precheck = new File(localPath, fileName);
                if(precheck.exists()){
                    precheck.delete();
                }
                FileUtils.moveFile(file, new File(localPath, fileName));
                git.add().addFilepattern(fileName).call();
                Logger.debug("add file finish"+fileName);
                git.commit().setMessage(commit).setAuthor(semesteruser.lastname,semesteruser.email).call();
                Logger.warn("start pushing");
                RefSpec refSpec = new RefSpec("master");
                git.push().setRemote("origin").setRefSpecs(refSpec).call();
                git.getRepository().close();
                assignment.handin=new Date();
                assignment.setishandin();
                assignment.update(semester);

                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
            } else {
                flash("danger", Messages.get("Lecture.assignment.uploadfail"));
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
            }
        }catch(Exception e){
            Logger.warn("uploadnewhomework exception: "+e.getMessage());
            flash("danger", Messages.get("Lecture.assignment.uploadfail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        }

    }

    public static Result reverthandinhomework(String assignmentid,String user,String semester,String lecturename){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);



        try {
            Repo repo=Repo.findRepoByLectureAndOwner(assignment.semester,semesteruser,assignment.lecture);
            File localPath = File.createTempFile(reponame(assignment.lecture, semesteruser), "");
            localPath.delete();
            Logger.debug("Cloning from "+repo.repofilepath+"to"+localPath);
            Git git = Git.cloneRepository()
                    .setURI(repo.repofilepath)
                    .setDirectory(localPath)
                    .call();
            Ref head = git.getRepository().getRef("refs/heads/master");
            RevWalk walk=new RevWalk(git.getRepository());
            RevCommit commit = walk.parseCommit(head.getObjectId());
            Logger.debug("revert last commit");
            git.revert().include(commit).call();
            RefSpec refSpec = new RefSpec("master");
            git.push().setRemote("origin").setRefSpecs(refSpec).call();
            git.getRepository().close();
            flash("success",Messages.get("Lecture.assignment.revertsuccess"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        } catch (Exception e) {
            Logger.warn("Exceptione revert commit"+e.getMessage());
            flash("danger", Messages.get("Lecture.assignment.revertfail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        }
    }

}
