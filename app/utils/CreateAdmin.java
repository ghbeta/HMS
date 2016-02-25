package utils;

import models.User;
import models.UserRoll;
import org.apache.commons.io.FileUtils;
import play.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Hao on 2015/11/9.
 */
public class CreateAdmin {
    public static boolean createadmin(){
        Path p= Paths.get(System.getProperty("user.home"), "HMS_Config", "config.hms");
        File config=p.toFile();
        Logger.warn("analysing super user config at "+p.toString());
        if(config.exists()&&User.findByEmail("hms@hms.com","global")!=null){
            return true;
        }
        else{
            System.out.println("There is no super user for hms system, create standard super user,pls login and change it after system online");

            System.out.println("creating email");
            String email="hms@hms.com";
            //User user=User.findByEmail(email,"global");
            System.out.println("creating password");
            String password= UUID.randomUUID().toString();
            User sysadmin=new User();
//            while(user!=null&& !EmailValidator.getInstance().isValid(email)){
//                System.out.println("This email has already been used or is a invalid email adress,pls try another one: ");
//                email=in.next();
//                user=User.findByEmail(email,"global");
//            }


            sysadmin.email=email;
            try{
            sysadmin.passwordHash=Hash.createPassword(password);
                sysadmin.setUserHash();
                sysadmin.dateCreation=new Date();
                sysadmin.validated=true;
                sysadmin.setUserHash();
                sysadmin.id="admin"+UUID.randomUUID().toString();
                sysadmin.lastname="HMS";
                sysadmin.firstname="ADMIN";
                sysadmin.roles= UserRoll.SystemAdmin.toString();
                if(User.findByEmail(email,"global")!=null){
                sysadmin.update("global");}
                else{
                    sysadmin.save("global");
                }
                System.out.println("Super admin is created");
                FileUtils.deleteQuietly(config);
                FileUtils.writeStringToFile(config,email);
                FileUtils.writeStringToFile(config,password,true);
                return true;
            }catch(Exception e){
                System.out.println(e.getMessage());
                return false;
            }


        }


    }
}
