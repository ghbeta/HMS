package controllers.lectures.admin;

import com.jcraft.jsch.Session;
import models.Lecture;
import models.Repo;
import models.Semesteruser;
import models.User;
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
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import Permission.Securedassistant;
import java.io.IOException;

import static utils.RepoManager.adminrepofilepath;
import static utils.RepoManager.configrepopath;

/**
 * Created by Hao on 2015/11/5.
 */
public class Assignmentevaluation extends Controller {

    @Security.Authenticated(Securedassistant.class)
    public static boolean grandaccess(Semesteruser student, Lecture lecture) {
        User currentadmin= User.findByEmail(ctx().session().get("email"),lecture.semester);
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        Logger.debug("admin repo path" + ctx().request().getHeader("Host") + System.getProperty("user.home"));
        //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
        if(!currentadmin.sshs.isEmpty()) {
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
}
