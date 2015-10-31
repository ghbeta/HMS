package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import static com.avaje.ebean.Ebean.getServer;
import javax.persistence.*;

/**
 * Created by Hao on 2015/7/15.
 */
@Entity
@Table(name = "repos")
public class Repo extends Model {
    @Id
    @GeneratedValue
    public String id;

    @Constraints.Required
    public String repopath;

    public void setRepofilepath(String reponame) {
        this.repofilepath = System.getProperty("user.home")+"/repositories/"+reponame+".git";
    }

    public String repofilepath;

    @ManyToOne//(cascade= CascadeType.ALL)
    //@JoinColumn
    public Semesteruser owner;

    @Version
    private Long version;

    @ManyToOne//(cascade= CascadeType.ALL)
    //@JoinColumn
    public Lecture course;

    public String semester;


   public static Repo findRepoByLectureAndOwner(String semester,Semesteruser semesteruser,Lecture lecture){
       Repo repo=getServer(semester).find(Repo.class).fetch("owner").fetch("course").where().eq("owner.id",semesteruser.id).eq("course.courseName",lecture.courseName).findUnique();
       if(semesteruser.repos.contains(repo)){
       return repo;}
       else {
           return null;
       }
   }


//    @Override
//    public void save(String semester){
//        Ebean.getServer(semester).save(this);
//
//    }

}
