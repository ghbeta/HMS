package controllers.lectures.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.jcraft.jsch.Session;
import models.*;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.ConfigManager;
import nl.minicom.gitolite.manager.models.Permission;
import org.apache.commons.mail.EmailException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import Permission.Securedassistant;
import utils.Mail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import static utils.RepoManager.adminrepofilepath;
import static utils.RepoManager.configrepopath;
import static utils.RepoManager.reponame;
import static utils.UploadPath.localrepopullpath;

/**
 * Created by Hao on 2015/11/5.
 */
public class Assignmentevaluation extends Controller {

    @Security.Authenticated(Securedassistant.class)
    public static boolean grandaccess(Semesteruser currentadmin,Semesteruser student, Lecture lecture) {
        User admincredential= User.findByEmail(ctx().session().get("email"),"global");
        Repo studentrepo=student.getRepoByLecture(lecture);
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        Logger.debug("admin repo path" + ctx().request().getHeader("Host") + System.getProperty("user.home"));
        //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
        if(!admincredential.sshs.isEmpty()&&!studentrepo.owner.contains(currentadmin)) {
            try {
                Repository adminrepo = new FileRepository(adminrepofilepath());
                Git gitogit = new Git(adminrepo);
                ConfigManager manager = ConfigManager.create(configrepopath());
                Config config = manager.get();
                nl.minicom.gitolite.manager.models.User repoadmin = config.ensureUserExists(currentadmin.userHash);

                String reponame = lecture.courseName + "_" + student.userHash;
                Logger.warn("grand access for repo" +reponame);
                nl.minicom.gitolite.manager.models.Repository repository = config.ensureRepositoryExists(lecture.courseName + "_" + student.userHash);
                repository.setPermission(repoadmin, Permission.READ_WRITE);
                Logger.warn("add admin user to student repo now");
                manager.applyAsync(config);
                gitogit.pull().call();
                gitogit.push().call();
                studentrepo.owner.add(currentadmin);
                studentrepo.update(lecture.semester);
                Logger.warn("grand success");
                return true;
            } catch (Exception e) {
                Logger.warn(e.getMessage());
                return false;
            }
        }
       else{
            return false;
        }

    }

    public static class EvaluationForm{
        @Constraints.Required
        public float pointsearnd;

        public String evalcomments;
    }

    @Security.Authenticated(Securedassistant.class)
    public static Result addevaluation(String semester,String lecturename,String assignment,String student){
       //JsonNode json = request().body().asJson();
       Form<EvaluationForm> evaldata = Form.form(EvaluationForm.class).bindFromRequest();

        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser marker=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        if(!evaldata.hasErrors()){
        Semesteruser students=Semesteruser.findByEmail(student, semester);
        Assignment currentassignment=Assignment.findById(semester,assignment);
        Handin currenthandin=Handin.getHandinofassignmentofstudentinlecture(semester,currentlecture,students,currentassignment);
        Evaluation eval=Evaluation.findByLectureAndUser(semester, currentlecture, students);
//        Iterator<JsonNode> iter=json.elements();
//        int i=0;
//        int j=0;
//        while(iter.hasNext()) {
//            if (i%3 == 0) {
//              j=j+1;
//               // i=i+1;
//                //Logger.warn("add exercise number "+(j-1)+" totalpoints "+iter.next().findPath("value").textValue());
//                currenthandin.exercises.get(j-1).earndpoints= Float.parseFloat(iter.next().findPath("value").textValue());
//
//
//            }
////
//            if(i%3==1){
//                //i=i+1;
//                //Logger.warn("add exercise number "+(j-1)+" earndpoints "+iter.next().findPath("value").floatValue());
//                currenthandin.exercises.get(j-1).totalpoints= Float.parseFloat(iter.next().findPath("value").textValue());
//            }
////
//            if(i%3==2){
//                //i=i+1;
//                //Logger.warn("add exercise number "+(j-1)+" comments "+iter.next().findPath("value").textValue());
//                currenthandin.exercises.get(j-1).comments=iter.next().findPath("value").textValue();
//                //currenthandin.exercises.get(j-1).update(semester);
//            }
////        i++;
////
////          System.out.println(i%3);
//           i++;
//           //Logger.warn(""+i);
//            //Logger.warn("passed json data " + iter.next().findPath("value").textValue());
//        }
        currenthandin.marker=marker;
        currenthandin.isevaluated=true;
        currenthandin.setEarndpoints(evaldata.get().pointsearnd);
        currenthandin.comments=evaldata.get().evalcomments;
        currenthandin.setTotalpoints();
        currenthandin.setIsvalid();
        currenthandin.update(semester);
        eval.setPerformance(semester,currentlecture,students);
        eval.update(semester);
        String localrepopath=localrepopullpath(semester,lecturename,students.id,reponame(currentlecture, students));
        try {
            Repository repository=new FileRepository(localrepopath+"/.git");
            Git git = new Git(repository);
            Logger.debug("starting merging students remote to local");
            git.pull().call();
            sendMailForEvaluation(students,currenthandin);
        } catch (Exception e) {
            e.getMessage();
        }
        return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(marker.lastname,semester,currentlecture.courseName));}
        else
        {
            flash("danger",Messages.get("assignment.eval.fail"));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(marker.lastname, semester, currentlecture.courseName));
        }
    }

    private static void sendMailForEvaluation(Semesteruser semesteruser, Handin handin) throws EmailException,MalformedURLException {
        String subject = Messages.get("mail.evaluation.done.subject");
        String message= Messages.get("email.handin.message")+handin.assignment.title+" "+handin.lecture.courseName+Messages.get("email.handin.on")+handin.handin+Messages.get("email.handin.isevaluated")+handin.marker.lastname;
        Mail.Envelop envelop = new Mail.Envelop(subject, message, semesteruser.email);
        Mail.sendMail(envelop);
    }
}
