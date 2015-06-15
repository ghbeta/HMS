package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Hao on 2015/5/17.
 */
@Entity
public class User extends Model{
   public static final String DBServer="global";
    @Id
    public String matrikel;


    public String firstname;
    public String lastname;
    public String sha1;

    public static Finder<String,User> find = new Finder<String, User>(String.class,User.class);

    @Override
    public void save() {
       Ebean.getServer(DBServer).save(this);
       // super.save();
    }
}
