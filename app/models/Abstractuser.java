package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Created by Hao on 2015/10/8.
 */
@MappedSuperclass
public class Abstractuser extends Model {
    @Id
    @Column(unique = true)
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

    public String userHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateLastlogin;

    @Formats.NonEmpty
    public Boolean validated = false;

    @Column(unique = true)
    public String ssh;
}
