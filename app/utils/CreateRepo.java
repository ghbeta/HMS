package utils;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import models.Lecture;
import models.Repo;
import models.Semesteruser;
import models.User;
import nl.minicom.gitolite.manager.exceptions.GitException;
import nl.minicom.gitolite.manager.exceptions.ModificationException;
import nl.minicom.gitolite.manager.exceptions.ServiceUnavailable;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.ConfigManager;
import nl.minicom.gitolite.manager.models.Permission;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import play.Logger;

import java.io.IOException;

import static play.mvc.Controller.ctx;

/**
 * Created by Hao on 2015/10/28.
 */
public class CreateRepo {
    public static String createRemoteRepo(User user, Lecture lecture,String serverhost) throws ServiceUnavailable, IOException, GitException, ModificationException, GitAPIException {
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        System.out.println(ctx().request().getHeader("Host")+System.getProperty("user.home"));
        //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
        Repository adminrepo = new FileRepository(System.getProperty("user.home")+"/gitolite-admin"+"/.git");
        Git gitogit = new Git(adminrepo);
        ConfigManager manager = ConfigManager.create(System.getProperty("user.home")+"/gitolite-admin");
        Config config = manager.get();
        nl.minicom.gitolite.manager.models.User repouser=config.ensureUserExists(user.userHash);
        nl.minicom.gitolite.manager.models.User admin=config.getUser("admin");


        String reponame=lecture.courseName+"_"+user.userHash;
        nl.minicom.gitolite.manager.models.Repository repository = config.ensureRepositoryExists(lecture.courseName+"_"+user.userHash);
        repository.setPermission(repouser, Permission.ALL);
        repository.setPermission(admin, Permission.READ_ONLY);

        Logger.warn("size"+user.sshs.size());
        if(!user.sshs.isEmpty()){
            for(int i=0;i<user.sshs.size();i++){
                Logger.warn("add repo now");
                repouser.setKey(user.sshs.get(i).title, user.sshs.get(i).ssh);

            }
            manager.applyAsync(config);
            gitogit.pull().call();
            gitogit.push().call();
            return "git address: git@"+ hostparser(serverhost)+":"+reponame+".git";
        }
        else{
                return null;
        }

    }
    public static boolean deleteRepo(User user, Lecture lecture,String serverhost) throws IOException, ServiceUnavailable, GitException, GitAPIException {
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(lecture.semester,user);
        String reponame=lecture.courseName+"_"+user.userHash;
        Repository adminrepo = new FileRepository(System.getProperty("user.home")+"/gitolite-admin"+"/.git");
        Git gitogit = new Git(adminrepo);
        ConfigManager manager = ConfigManager.create(System.getProperty("user.home")+"/gitolite-admin");
        Config config = manager.get();
        nl.minicom.gitolite.manager.models.Repository repotodelete = config.getRepository(reponame);

        if(config.removeRepository(repotodelete)){
            Logger.warn("start delete repo");
            manager.applyAsync(config);
            gitogit.pull().call();
            gitogit.push().call();
            Repo repo=Repo.findRepoByLectureAndOwner(lecture.semester,semesteruser,lecture);
            try{
            repo.delete(lecture.semester);
            return true;}
            catch (Exception e){
                return false;
            }
        }else{
            return false;
        }
    }
    public static String hostparser(String serverhost){
         if(serverhost.contains(":")){
             String[] parts=serverhost.split(":");
             return parts[0];
         }else{
             return serverhost;
         }



    }
}
