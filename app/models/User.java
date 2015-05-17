package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Hao on 2015/5/17.
 */
@Entity
public class User extends Model{
    @Id
    public String matrikel;


    public String firstname;
    public String lastname;
    public String sha1;

}
