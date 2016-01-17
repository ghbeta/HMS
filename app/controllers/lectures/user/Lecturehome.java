package controllers.lectures.user;

import Permission.Securedstudents;
import com.jcraft.jsch.Session;
import models.*;
import net.lingala.zip4j.core.ZipFile;
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
import play.libs.Json;
import play.mvc.*;
import utils.RepoManager;
import views.html.lectures.user.lecturehome;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import controllers.lectures.user.routes;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static utils.FileWatcher.InitWatchService.getWatchService;
import static utils.FileWatcher.InitWatchService.registerALL;
import static utils.RepoManager.hostparser;
import static utils.RepoManager.reponame;
import static utils.RepoManager.userrepofilepath;
import static utils.UploadPath.localrepopullpath;
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
        return ok(lecturehome.render(currentuser, currentsemesteruser, selectedlecture,null,null));}
        else
        {
            flash("danger", Messages.get("lecture.home.expired"));
            return redirect(controllers.lectures.routes.Lecturehub.myLectures());
        }

    }


    @Security.Authenticated(Securedstudents.class)
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
           newrepo.owner.add(semesteruser);
           newrepo.repopath=repopath;
           newrepo.semester=lecture.semester;
           newrepo.setRepofilepath(reponame(lecture,semesteruser));
           newrepo.save(lecture.semester);
           Logger.warn("new repo saved redirect");
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
//                if(!semesteruser.assignments.containsAll(lecture.assignments)){
//            semesteruser.assignments.addAll(lecture.assignments);}
                Evaluation eval= new Evaluation();
                eval.lecture=lecture;
                eval.student=semesteruser;
                eval.save(semester);
                semesteruser.update(semester);
            try{
                String repopath= RepoManager.createRemoteRepo(currentuser, lecture, request().getHeader("Host"));
                if(repopath!=null){
                    Repo newrepo = new Repo();
                    newrepo.course=lecture;
                    newrepo.owner.add(semesteruser);
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
            try{
                RepoManager.deleteRepo(currentuser, lecture, request().getHeader("Host"));
                Evaluation eval=Evaluation.findByLectureAndUser(semester, lecture, semesteruser);
                eval.delete(semester);
                List<Handin> handins=Handin.getAllHandinofStudentsinLecture(semester,lecture,semesteruser);
                semesteruser.handins.removeAll(handins);
                Iterator<Handin> iter=handins.iterator();
                while(iter.hasNext()){
                    iter.next().delete(semester);
                }

                semesteruser.update(semester);//todo test here
                //return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
                if(Lecture.deleteSemesteruserfromLecture(semester, semesteruser, lecture))
                {
                    return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname,semester,lecture.courseName));
                }
                else
                {
                    flash("danger", Messages.get("lecture.deleteuser.fail"));
                    return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
                }
            }catch (Exception e){
                flash("danger", Messages.get("lecture.deleteuser.fail"));
                return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, semester, lecture.courseName));
            }

    }

    @Security.Authenticated(Securedstudents.class)
    @BodyParser.Of(BodyParser.Json.class)
    public static Result lastUpdateStatus(String owneremail,String course,String semester){
        Lecture lecture=Lecture.getlecturebyname(course,semester);
        Semesteruser semesteruser=Semesteruser.findByEmail(owneremail,semester);
        String serverhost=request().getHeader("Host");
        String reponame=lecture.semester+"_"+lecture.courseName+"_"+semesteruser.id;
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

           try (RevWalk walk=new RevWalk(repository)) {
//               RevCommit commit = walk.parseCommit(head.getObjectId());
//               walk.markStart(commit);
//
//               //System.out.println("Commit-Message: " + commit.getFullMessage());
//               walk.dispose();
////      if(commit.getFullMessage().isEmpty()||commit.getFullMessage()==null){
////          return Messages.get("Localrepo.status.none");
////      }
////               else{
////
////       return "Commit-Message: " + commit.getFullMessage();}
//           }}
               //List<Ref> branches=git.branchList().call();//branches
               List<String> allcommit=new ArrayList<>();

                   RevCommit commit=walk.parseCommit(head.getObjectId());
                   walk.markStart(commit);
                  for(RevCommit rev:walk){
                      allcommit.add(rev.getFullMessage());
                  }
                 walk.dispose();
               return ok(Json.toJson(allcommit));
           }catch(Exception e){
               Logger.warn("walking"+ e.getMessage());
               return badRequest();
           }

           }
       catch(Exception e){
           Logger.warn( e.getMessage());
           return badRequest();
       }

    }

    @Security.Authenticated(Securedstudents.class)
    public static Result handinhomeworkremote(String assignmentid,String user,String semester,String lecturename){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        Lecture lecture = Lecture.getlecturebyname(lecturename,semester);
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        DynamicForm handinform = Form.form().bindFromRequest();
        try{
        if(Handin.getHandinofassignmentofstudentinlecture(semester,lecture,semesteruser,assignment)!=null){
            Handin handin=Handin.getHandinofassignmentofstudentinlecture(semester, lecture, semesteruser, assignment);
            handin.handin=new Date();
            handin.setishandin();
            handin.update(semester);
        }else{
            Handin handin= new Handin();
            handin.student=semesteruser;
            handin.lecture=lecture;
            handin.assignment=assignment;
            //handin.exercises=new ArrayList<Exercise>();
//            for(int i=0;i<assignment.numberofexercise;i++){
//                Exercise exercise = new Exercise();
//                exercise.title=Messages.get("exercise.title")+(i+1);
//                exercise.semester=semester;
//                //exercise.handin=handin;
//                //exercise.save(semester);
//                //exercises.add(exercise);
//                handin.exercises.add(exercise);
//                //exercise.handin=handin;
//            }

            handin.handin=new Date();
            handin.setishandin();
            handin.isevaluated=false;
            //assignment.handin=new Date();
            //assignment.setishandin();
            //handin.
            handin.save(semester);}

            Repo repo=Repo.findRepoByLectureAndOwner(assignment.semester,semesteruser,assignment.lecture);
            File localPath = new File(localrepopullpath(semester,lecturename,currentuser.id,reponame(assignment.lecture, semesteruser)), "");
            //localPath.delete();
            if(localPath.exists()) {
                FileUtils.forceDelete(localPath);
            }

            Logger.debug("Cloning from "+repo.repofilepath+"to"+localPath);
            Git git = Git.cloneRepository()
                    .setURI(repo.repofilepath)
                    .setDirectory(localPath)
                    .call();
            //Logger.debug("create init commit");
            //git.commit().setMessage("init commit").setAuthor(semesteruser.lastname,semesteruser.email).call();
            Logger.debug("create local repo: "+git.getRepository().getDirectory());

        Logger.warn("create handin success");
        return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));}
        catch (Exception e){
            flash("danger", Messages.get("Lecture.assignment.uploadfail"));
            Logger.warn(e.getMessage());
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        }
    }

    @Security.Authenticated(Securedstudents.class)
    public static Result handinhomework(String assignmentid,String user,String semester,String lecturename){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        Lecture lecture = Lecture.getlecturebyname(lecturename,semester);
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        DynamicForm handinform = Form.form().bindFromRequest();
        String commit="";
        String des=assignment.title+"/";
        if(handinform.get("commit")==null||handinform.get("commit").isEmpty()||handinform.get("commit").equals("")){
            commit  =assignment.title+"_"+Messages.get("lecture.uploadsolution");
        }else{
            commit = assignment.title+"_"+handinform.get("commit");
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
                String localrepopath=localrepopullpath(semester,lecturename,currentuser.id,reponame(assignment.lecture, semesteruser));
                File localPath = new File(localrepopath, "");
                Git git=null;
                //localPath.delete();
                if(!localPath.exists()) {
                    //FileUtils.forceDelete(localPath);
                    Logger.debug("not exist should create local repo");
                    Logger.debug("Cloning from "+repo.repofilepath+"to"+localPath);
                    git = Git.cloneRepository()
                            .setURI(repo.repofilepath)
                            .setDirectory(localPath)
                            .call();
                }
                else{
                    Repository repository=new FileRepository(localrepopath+"/.git");
                    git = new Git(repository);
                    Logger.debug("starting merging remote to local");
                    git.pull().call();
                }



                //Logger.debug("create init commit");
                //git.commit().setMessage("init commit").setAuthor(semesteruser.lastname,semesteruser.email).call();
                Logger.debug("create local repo: "+git.getRepository().getDirectory());
                File precheck = new File(localPath, des+fileName);
                if(precheck.exists()){
                    //precheck.delete();
                    FileUtils.forceDelete(precheck);
                }
                FileUtils.moveFile(file, new File(localPath, des+fileName));
                ZipFile zipFile= new ZipFile(new File(localPath, des+fileName));
                if(zipFile.isValidZipFile()){
                zipFile.extractAll(localrepopath+"/"+des);
                File todelte=new File(localPath, des+fileName);
                FileUtils.forceDelete(todelte);}
                git.add().addFilepattern(assignment.title).call();
                Logger.debug("add file finish"+des+fileName);
                git.commit().setMessage(commit).setAuthor("hms","hms@hms.com").call();
                Logger.warn("start pushing");
                RefSpec refSpec = new RefSpec("master");
                git.push().setRemote("origin").setRefSpecs(refSpec).call();
                git.getRepository().close();
                Logger.warn("start create handin");
                if(Handin.getHandinofassignmentofstudentinlecture(semester,lecture,semesteruser,assignment)!=null){
                    Handin handin=Handin.getHandinofassignmentofstudentinlecture(semester, lecture, semesteruser, assignment);
                    handin.handin=new Date();
                    handin.setishandin();
                    handin.update(semester);
                }else{
                Handin handin= new Handin();
                handin.student=semesteruser;
                handin.lecture=lecture;
                handin.assignment=assignment;
                //handin.exercises=new ArrayList<Exercise>();
//                for(int i=0;i<assignment.numberofexercise;i++){
//                    Exercise exercise = new Exercise();
//                    exercise.title=Messages.get("exercise.title")+(i+1);
//                    exercise.semester=semester;
//                    //exercise.handin=handin;
//                    //exercise.save(semester);
//                    //exercises.add(exercise);
//                    handin.exercises.add(exercise);
//                    //exercise.handin=handin;
//                }

                handin.handin=new Date();
                handin.setishandin();
                handin.isevaluated=false;
                //assignment.handin=new Date();
                //assignment.setishandin();
                //handin.
                handin.save(semester);}
                Logger.warn("create handin success");
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

    @Security.Authenticated(Securedstudents.class)
    public static Result reverthandinhomeworkremote(String assignmentid,String user,String semester,String lecturename){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        Lecture lecture = Lecture.getlecturebyname(lecturename,semester);
        try {
            Handin handin=Handin.getHandinofassignmentofstudentinlecture(semester,lecture,semesteruser,assignment);
            if(handin!=null&&!handin.isevaluated){
                handin.delete(semester);
                flash("success",Messages.get("Lecture.assignment.revertsuccess"));}
            if(handin==null||handin.isevaluated){
                flash("danger", Messages.get("Lecture.assignment.revertfail"));
            }

            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        } catch (Exception e) {
            Logger.warn("Exceptione revert commit"+e.getMessage());
            flash("danger", Messages.get("Lecture.assignment.revertfail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        }
    }

    @Security.Authenticated(Securedstudents.class)
    public static Result reverthandinhomework(String assignmentid,String user,String semester,String lecturename){
        Assignment assignment=Assignment.findById(semester,assignmentid);
        User currentuser=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,currentuser);
        Lecture lecture = Lecture.getlecturebyname(lecturename,semester);
        String commit="delete last upload of "+assignment.title;

        try {
            Repo repo=Repo.findRepoByLectureAndOwner(assignment.semester,semesteruser,assignment.lecture);
            File localPath = File.createTempFile(reponame(assignment.lecture, semesteruser), "");
            localPath.delete();
            Logger.debug("Cloning from "+repo.repofilepath+"to"+localPath);
            Git git = Git.cloneRepository()
                    .setURI(repo.repofilepath)
                    .setDirectory(localPath)
                    .call();
            Logger.debug("create local repo: "+git.getRepository().getDirectory());
            String subfolder=assignment.title;

            Logger.debug("delete folder"+subfolder);
            git.rm().addFilepattern(subfolder).call();
            git.commit().setMessage(commit).setAuthor("hms","hms@hms.com").call();
            Logger.warn("start pushing delete files");

            RefSpec refSpec = new RefSpec("master");
            git.push().setRemote("origin").setRefSpecs(refSpec).call();
            git.getRepository().close();
            Handin handin=Handin.getHandinofassignmentofstudentinlecture(semester,lecture,semesteruser,assignment);
            if(handin!=null&&!handin.isevaluated){
            handin.delete(semester);
                flash("success",Messages.get("Lecture.assignment.revertsuccess"));}
            if(handin==null||handin.isevaluated)
            {
                flash("danger", Messages.get("Lecture.assignment.revertfail"));
            }

            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        } catch (Exception e) {
            Logger.warn("Exceptione revert commit"+e.getMessage());
            flash("danger", Messages.get("Lecture.assignment.revertfail"));
            return redirect(routes.Lecturehome.generatelecturehome(semesteruser.lastname, assignment.semester, assignment.lecture.courseName));
        }
    }

}
