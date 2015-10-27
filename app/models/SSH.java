package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import static com.avaje.ebean.Ebean.getServer;
/**
 * Created by Hao on 2015/10/27.
 */
@Entity
@Table(name = "ssh")
public class SSH extends Model {

    @Id
    @GeneratedValue
    public String id;

    @Column(unique = true,length = 65535)
    @Constraints.Required
    public String ssh;

    @Constraints.Required
    public String title;

    @ManyToOne
    public User sshowner;

    public static SSH findById(String id){
        return getServer("global").find(SSH.class).where().eq("id",id).findUnique();
    }
}
