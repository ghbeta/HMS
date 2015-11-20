package controllers.account.settings;

import Permission.Secureddefault;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import models.SSH;
import models.User;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.RepoManager;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

/**
 * Created by Hao on 2015/10/27.
 */
@Security.Authenticated(Secureddefault.class)
public class ModifySSH extends Controller{



    public static Result addssh (){
        Form<SSH> sshform = Form.form(SSH.class).bindFromRequest();
        if(!sshform.hasErrors()){
        //String title=sshform.get().title;
        //String ssh=sshform.get().ssh;
        User user = User.findByEmail(request().username(),"global");
        SSH ssh= new SSH();
            ssh.title=sshform.get().title;
            ssh.ssh=sshform.get().ssh;

            if(RepoManager.addSSHtoUser(user,ssh)!=null&&!user.sshs.contains(ssh)){

                try{
                user.sshs.add(ssh);
                user.update("global");
                flash("success",Messages.get("ssh.addkey.success"));
                return redirect(routes.Index.index());}
                catch(Exception e){
                    Logger.warn(e.getMessage());
                    flash("danger",Messages.get("ssh.addkey.fail.exist"));
                    return redirect(routes.Index.index());
                }
            }else{
                flash("danger",Messages.get("ssh.addkey.fail"));
                return redirect(routes.Index.index());
            }
        //user.update("global");
        }
        else{
            flash("danger", Messages.get("ssh.form.empty"));
            return redirect(routes.Index.index());
        }
    }

    public static Result deletessh(String sshid){
       User user = User.findByEmail(request().username(),"global");
        try{
            SSH ssh= SSH.findById(sshid);
            Logger.info("delete ssh key "+ssh.title);
            ssh.delete("global");
            if(RepoManager.deleteSSHfromUser(user,ssh)!=null){
                flash("success",Messages.get("ssh.delete.success"));


             return redirect(routes.Index.index());
            }
        else{
                flash("danger",Messages.get("ssh.delete.fail"));
                return redirect(routes.Index.index());
            }}
        catch (Exception e){
            flash("danger",Messages.get("ssh.delete.fail"));
            return redirect(routes.Index.index());
        }
    }
}
