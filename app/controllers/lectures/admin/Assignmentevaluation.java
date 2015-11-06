package controllers.lectures.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.jcraft.jsch.Session;
import models.*;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.ConfigManager;
import nl.minicom.gitolite.manager.models.Permission;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import Permission.Securedassistant;
import java.io.IOException;
import java.util.Iterator;

import static utils.RepoManager.adminrepofilepath;
import static utils.RepoManager.configrepopath;

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
                repository.setPermission(repoadmin, Permission.READ_ONLY);
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

    @BodyParser.Of(BodyParser.Json.class)
    @Security.Authenticated(Securedassistant.class)
    public static Result addevaluation(String semester,String lecturename,String assignment,String student){
        JsonNode json = request().body().asJson();
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        Semesteruser marker=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Semesteruser students=Semesteruser.findByEmail(student, semester);
        Assignment currentassignment=Assignment.findById(semester,assignment);
        Handin currenthandin=Handin.getHandinofassignmentofstudentinlecture(semester,currentlecture,students,currentassignment);
        Iterator<JsonNode> iter=json.elements();
        int i=0;
        int j=0;
        while(iter.hasNext()) {
            if (i / 3 == 0) {
              j=j+1;
                Logger.warn("add exercise number "+(j-1)+" totalpoints "+iter.next().findPath("value").floatValue());
                currenthandin.exercises.get(j-1).totalpoints=iter.next().findPath("value").floatValue();
            }

            if(i/3==1){
                Logger.warn("add exercise number "+(j-1)+" earndpoints "+iter.next().findPath("value").floatValue());
                currenthandin.exercises.get(j-1).earndpoints=iter.next().findPath("value").floatValue();
            }

            if(i/3==2){
                Logger.warn("add exercise number "+(j-1)+" comments "+iter.next().findPath("value").textValue());
                currenthandin.exercises.get(j-1).comments=iter.next().findPath("value").textValue();
                currenthandin.exercises.get(j-1).update(semester);
            }
        i++;

            //Logger.warn("passed json data " + iter.next().findPath("value").textValue());
        }
        currenthandin.marker=marker;
        currenthandin.update(semester);
        return ok(json.toString());
    }
}
