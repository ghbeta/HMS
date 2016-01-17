package utils;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import models.*;
import nl.minicom.gitolite.manager.exceptions.GitException;
import nl.minicom.gitolite.manager.exceptions.ModificationException;
import nl.minicom.gitolite.manager.exceptions.ServiceUnavailable;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.ConfigManager;
import nl.minicom.gitolite.manager.models.Permission;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import play.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import static play.mvc.Controller.ctx;
import static utils.FileWatcher.InitWatchService.getWatchService;

/**
 * Created by Hao on 2015/10/28.
 */
public class RepoManager {
    public static String createRemoteRepo(User user, Lecture lecture,String serverhost) throws ServiceUnavailable, IOException, GitException, ModificationException, GitAPIException {
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        Logger.debug("admin repo path"+ctx().request().getHeader("Host")+System.getProperty("user.home"));
        //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
        Repository adminrepo = new FileRepository(adminrepofilepath());
        Git gitogit = new Git(adminrepo);
        ConfigManager manager = ConfigManager.create(configrepopath());

        Config config = manager.get();
        nl.minicom.gitolite.manager.models.User repouser=config.ensureUserExists(user.id);
        nl.minicom.gitolite.manager.models.User admin=config.getUser("admin");


        String reponame=lecture.semester+"_"+lecture.courseName+"_"+user.id;
        nl.minicom.gitolite.manager.models.Repository repository = config.ensureRepositoryExists(reponame);
        if(!lecture.localrepo){
        repository.setPermission(repouser, Permission.ALL);}
        repository.setPermission(admin, Permission.READ_ONLY);

        Logger.warn("size"+user.sshs.size());
        if(!user.sshs.isEmpty()||lecture.localrepo){

            Logger.warn("add repo now");
            manager.applyAsync(config);
            gitogit.pull().call();
            gitogit.push().call();

            return "git@"+ hostparser(serverhost)+":"+reponame+".git";
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
        String reponame=lecture.semester+"_"+lecture.courseName+"_"+user.id;
        Repository adminrepo = new FileRepository(adminrepofilepath());
        Git gitogit = new Git(adminrepo);
        ConfigManager manager = ConfigManager.create(configrepopath());
        //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
        Config config = manager.get();
        nl.minicom.gitolite.manager.models.Repository repotodelete = config.getRepository(reponame);

        if(config.removeRepository(repotodelete)){
            Logger.warn("start delete repo");
            manager.applyAsync(config);
            gitogit.pull().call();
            gitogit.push().call();
            Repo repo=Repo.findRepoByLectureAndOwner(lecture.semester,semesteruser,lecture);
            try{
                Logger.debug("repo file system path:" + repo.repofilepath);
                //FileUtils.deleteDirectory(new File(repo.repofilepath).d);
                File repofile=new File(repo.repofilepath);

                    Logger.debug("starting delete filesystem");
                   boolean success= FileUtils.deleteQuietly(repofile);
                Logger.debug("delete success?="+success);

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

    public static String userrepofilepath(String reponame){
        return System.getProperty("user.home")+"/repositories/"+reponame+".git";
    }

    public static String adminrepofilepath(){
        return System.getProperty("user.home")+"/gitolite-admin"+"/.git";
    }

    public static String configrepopath(){
        return System.getProperty("user.home")+"/gitolite-admin";
    }

    public static String reponame(Lecture lecture,Semesteruser semesteruser){
        return lecture.semester+"_"+lecture.courseName+"_"+semesteruser.id;
    }

    public static String addSSHtoUser(User user, SSH ssh){
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        try{
        Logger.debug("admin repo path"+ctx().request().getHeader("Host")+System.getProperty("user.home"));
        //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
        Repository adminrepo = new FileRepository(adminrepofilepath());
        Git gitogit = new Git(adminrepo);
        ConfigManager manager = ConfigManager.create(configrepopath());

        Config config = manager.get();
        nl.minicom.gitolite.manager.models.User repouser=config.ensureUserExists(user.id);
        repouser.setKey(ssh.title, ssh.ssh);
            manager.applyAsync(config);
            gitogit.pull().call();
            gitogit.push().call();
        return "success";}
        catch (Exception e){
            Logger.warn("exception at add ssh key to user "+e.getMessage());
            return null;
        }
    }

    public static String deleteSSHfromUser(User user,SSH ssh){
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
        try{
            Logger.debug("admin repo path"+ctx().request().getHeader("Host")+System.getProperty("user.home"));
            //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
            Repository adminrepo = new FileRepository(adminrepofilepath());
            Git gitogit = new Git(adminrepo);
            ConfigManager manager = ConfigManager.create(configrepopath());

            Config config = manager.get();
            nl.minicom.gitolite.manager.models.User repouser=config.ensureUserExists(user.id);
            repouser.removeKey(ssh.title);
            manager.applyAsync(config);
            gitogit.pull().call();
            gitogit.push().call();
            return "success";}
        catch (Exception e){
            Logger.warn("exception at delete ssh key from user "+e.getMessage());
            return null;
        }
    }

//    public static String deleteUserfromServer(User user){
//        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
//            @Override
//            protected void configure(OpenSshConfig.Host host, Session session) {
//                session.setConfig("StrictHostKeyChecking", "no");
//            }
//        });
//        try{
//            Logger.debug("admin repo path"+ctx().request().getHeader("Host")+System.getProperty("user.home"));
//            //ConfigManager manager = ConfigManager.create("git@localhost:gitolite-admin");
//            Repository adminrepo = new FileRepository(adminrepofilepath());
//            Git gitogit = new Git(adminrepo);
//            ConfigManager manager = ConfigManager.create(configrepopath());
//
//            Config config = manager.get();
//
//            nl.minicom.gitolite.manager.models.User repouser=config.getUser(user.userHash);
//            if(repouser!=null){
//                config.removeUser(repouser);
//                manager.applyAsync(config);
//                gitogit.pull().call();
//                gitogit.push().call();
//                return "success";
//            }
//            return "success";
//            }
//        catch (Exception e){
//            Logger.warn("exception at delete user from server "+e.getMessage());
//            return null;
//        }
//    }

}
