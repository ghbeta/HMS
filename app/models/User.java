package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hao on 2015/5/17.
 */
@Entity
@Table(name = "users")
public class User extends Model{
   public static final String DBServer="global";
    @Id
    public String matrikel;

    public static String getDBServer() {
        return DBServer;
    }

    public String getMatrikel() {
        return matrikel;
    }

    public void setMatrikel(String matrikel) {
        this.matrikel = matrikel;
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

    public String firstname;
    public String lastname;
    public String sha1;





    //public static Finder<String,User> find = new Finder<String, User>(DBServer,String.class,User.class);

    @Override
    public void save(String dbserver) {

       Ebean.getServer(dbserver).save(this);
       // super.save();
    }
}
