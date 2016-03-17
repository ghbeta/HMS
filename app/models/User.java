package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.AppException;
import utils.Hash;
import utils.MD5Util;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Hao on 2015/5/17.
 */
@Entity
@Table(name = "users")
public class User extends Abstractuser{
    public static final String DBServer="global";


    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateLastlogin;

    @Formats.NonEmpty
    public Boolean validated = false;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sshowner")
    public List<SSH> sshs;

//    public void changeEmail(String email,String database){
//        this.email=email;
//        this.save(database);
//    }

    public void setUserHash(){
        this.userHash= MD5Util.md5Hex(this.email);
    }








    //public static Finder<String,User> find = new Finder<String, User>(DBServer,String.class,User.class);
//--------- Start using Quries to get the information
//    @Override
//    public void save(String dbserver) {
//
//       Ebean.getServer(dbserver).save(this);
//       // super.save();
//    }

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    public static User findByEmail(String email,String database) {
        return currentServer(database).find(User.class).where().eq("email", email).findUnique();
    }

    public static User findById(String id,String database){
        return currentServer(database).find(User.class).where().eq("id",id).findUnique();
    }

    public static List<User> findByName(String lastname,String firstname,String database){
        if(lastname==null||lastname.isEmpty()){
        return currentServer(database).find(User.class).where().ieq("firstname",firstname).findList();}
        if(firstname==null||firstname.isEmpty()){
            return currentServer(database).find(User.class).where().ieq("lastname",lastname).findList();
        }
        else
        {
            return currentServer(database).find(User.class).where().ieq("firstname",firstname).ieq("lastname",lastname).findList();
        }
    }

    public static List<User> findAll(String database){
        return currentServer(database).find(User.class).findList();
    }

//    /**
//     * Retrieve a user from a fullname.
//     *
//     * @param fullname Full name
//     * @return a user
//     */
//    public static User findByFullname(String fullname,String database) {
//        return currentServer(database).find(User.class).where().eq("fullname", fullname).findUnique();
//    }

    /**
     * Retrieves a user from a confirmation token.
     *
     * @param token the confirmation token to use.
     * @return a user if the confirmation token is found, null otherwise.
     */
    public static User findByConfirmationToken(String token,String database) {
        return currentServer(database).find(User.class).where().eq("confirmationToken", token).findUnique();
    }

    /**
     * Authenticate a User, from a email and clear password.
     *
     * @param email         email
     * @param clearPassword clear password
     * @return User if authenticated, null otherwise
     * @throws AppException App Exception
     */
    public static User authenticate(String email, String clearPassword,String database) throws AppException {

        // get the user with email only to keep the salt password
        User user = currentServer(database).find(User.class).where().eq("email", email).findUnique();
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.passwordHash)) {
                return user;
            }
        }
        return null;
    }

    public void changePassword(String password,String database) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save(database);
    }

    /**
     * Confirms an account.
     *
     * @return true if confirmed, false otherwise.
     * @throws AppException App Exception
     */
    public static boolean confirm(User user,String database) throws AppException {
        if (user == null) {
            return false;
        }

        user.confirmationToken = null;
        user.validated = true;
        user.save(database);
        return true;
    }

}
