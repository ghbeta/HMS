package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Hao on 2015/7/15.
 */
@Entity
@Table(name = "Repos")
public class Repo extends Model {
    @Id
    String reponame;

    @ManyToOne
    User owner;

    @ManyToOne
    Course course;

    public String getReponame() {
        return reponame;
    }

    public void setReponame(String reponame) {
        this.reponame = reponame;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    @Override
    public void save(String semester){
        Ebean.getServer(semester).save(this);

    }

}
