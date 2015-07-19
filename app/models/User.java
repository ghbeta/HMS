package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.AppException;
import utils.Hash;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hao on 2015/5/17.
 */
@Entity
@Table(name = "users")
public class User extends Model{
   public static final String DBServer="global";
    @Id
    public String id;

    public String firstname;
    public String lastname;

    public String roles;

    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;


    public String sha1;

    public static String getDBServer() {
        return DBServer;
    }

    public String getMatrikel() {
        return id;
    }

    public void setMatrikel(String matrikel) {
        this.id = matrikel;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }







    //public static Finder<String,User> find = new Finder<String, User>(DBServer,String.class,User.class);
//--------- Start using Quries to get the information
    @Override
    public void save(String dbserver) {

       Ebean.getServer(dbserver).save(this);
       // super.save();
    }

    public static EbeanServer currentServer(String database){

        return Ebean.getServer(database);

    }
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
