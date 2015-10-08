package models;

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
    Semesteruser owner;

    @ManyToOne
    Lecture course;

    public String getReponame() {
        return reponame;
    }

    public void setReponame(String reponame) {
        this.reponame = reponame;
    }

    public Semesteruser getOwner() {
        return owner;
    }

    public void setSemesteruser(Semesteruser owner) {
        this.owner = owner;
    }

    public Lecture getCourse() {
        return course;
    }

    public void setCourse(Lecture course) {
        this.course = course;
    }


//    @Override
//    public void save(String semester){
//        Ebean.getServer(semester).save(this);
//
//    }

}
