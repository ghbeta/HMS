package controllers;

import com.avaje.ebean.Ebean;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import models.User;
import nl.minicom.gitolite.manager.exceptions.GitException;
import nl.minicom.gitolite.manager.exceptions.ModificationException;
import nl.minicom.gitolite.manager.exceptions.ServiceUnavailable;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.ConfigManager;
import nl.minicom.gitolite.manager.models.Group;
import nl.minicom.gitolite.manager.models.Permission;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import play.*;
import play.api.libs.Crypto;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;

import views.html.*;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import static play.libs.Json.toJson;
public class Application extends Controller {
    public static User currentuser = null;
    public static Result index() {
        return ok(index.render("HMS"));
        //return ok(register.render());
    }

    public static Result addUser(){

        User user = Form.form(User.class).bindFromRequest().get();
        user.sha1= Crypto.sign(user.matrikel);

        //Ebean.getServer();
        user.save();
        return ok(register.render());

        //return redirect(routes.Application.getUser());
    }
    public static Result addDatenbank(){


        return ok("test");
    }
    public static Result getUser(){
        String matrikelnummer = DynamicForm.form().bindFromRequest().get("matrikel");
        User user = User.find.byId(matrikelnummer);
        if(user != null){
        currentuser = user;
        return ok(createrepo.render());}
        else
        {return ok("User is not found");}
    }

    public static Result addRepo(){

       File gitDir = new File("localrepo/"+currentuser.sha1+"/.git");
        try {
            FileRepository repo = new FileRepository(gitDir);
            repo.create();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok("leave it on purpose");
    }

    public static Result addRemoteRepo() throws ModificationException, ServiceUnavailable, IOException, GitException, InterruptedException, GitAPIException, JSchException {
        //String host_current_user=System.getProperty("user.name");
        //System.out.println("llllllllllllllllllllllllllll"+host_current_user);

        DynamicForm requestData = Form.form().bindFromRequest();
        String host=requestData.get("hostname");
        String pubkey=requestData.get("pubkey");
        String repo = requestData.get("repo");
        Repository adminrepo = new FileRepository("/home/hao/gitolite-admin/.git");
        Git gitogit = new Git(adminrepo);
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking","no");
            }
        });
        ConfigManager manager = ConfigManager.create("/home/hao/gitolite-admin");
        //ConfigManager manager = ConfigManager.create("ssh://git@localhost:22/gitolite-admin");
        Config config = manager.get();

            nl.minicom.gitolite.manager.models.User repouser = config.createUser(currentuser.matrikel);

            config.createRepository(repo).setPermission(repouser, Permission.ALL);
            repouser.setKey(host,pubkey);

        manager.apply(config);
        gitogit.pull().call();
        //gitogit.commit().setMessage("Add User").call();
        gitogit.push().call();
//        JSch jsch = new JSch();
//        jsch.addIdentity("id_rsa");
//        Session session = jsch.getSession("git","localhost",22);
//        java.util.Properties prop = new java.util.Properties();
//        prop.put("StrictHostKeyChecking","no");
//        session.setConfig(prop);
//        session.connect();
//        SshSessionFactory factory = new JschConfigSessionFactory() {
//            @Override
//            protected void configure(OpenSshConfig.Host host, Session session) {
//                session.setConfig("StrictHostKeyChecking","no");
//            }
//            protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws  JSchException{
//                JSch jsch = super.getJSch(hc,fs);
//                jsch.removeAllIdentity();
//                jsch.addIdentity("id_rsa");
//                return jsch;
//            }
//        };

        //gitogit.push().call();






        return ok("git address: git@"+ InetAddress.getLocalHost()+":"+repo+".git");
    }
}
