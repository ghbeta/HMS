package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hao on 2015/10/8.
 */
@MappedSuperclass
public class Abstractuser extends Model {
    @Id
    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Column(unique = true)
    public String id;

    public String firstname;
    public String lastname;

    public String roles;


    public String userHash;








    public static EbeanServer currentServer(String database){

        return Ebean.getServer(database);

    }

}
