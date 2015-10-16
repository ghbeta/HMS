package models;

import com.avaje.ebean.Ebean;
import org.joda.time.DateTime;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.avaje.ebean.Ebean.getServer;

/**
 * Created by Hao on 2015/7/6.
 */
@Entity
@Table(name = "lecture")
public class Lecture extends Model {
    @Id
    @Column(unique = true)
    public String courseName;

    @Column(columnDefinition = "TEXT")
    public String desription;

    @Version
    private Long version;

    public boolean localrepo;
    public String closingdate;

    public boolean closed;
    public int totalassignment;
    public int optionalassignments;
    public int requriednumberofvalidassignment;
    public float requiredpercentfovalidassignment;

    public float minimumPercentageForExamination;

    public String semester;

    @OneToOne
    public Semesteruser lasteditor;
//
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "lecture")
    public List<Assignment> assignments;

    @OneToMany(cascade=CascadeType.ALL,mappedBy = "lecture")
    public List<Assignment> assignmentsoptional;

    @ManyToMany(cascade=CascadeType.ALL)
    public List<Semesteruser> attendent;
//
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "course")
    public List<Repo> repos;
//
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "lecture")
    public List<Message> messages;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Semesteruser getlasteditor() {
        return lasteditor;
    }

    public void setlasteditor(Semesteruser lasteditor) {
        this.lasteditor = lasteditor;
    }


    public static List<Lecture> getalllectures(String databasename){

        return getServer(databasename).find(Lecture.class).orderBy("courseName desc").findList();

    }

    public static List<Lecture> getalllecturesbyemail(String email,String databasename){
        return getServer(databasename).find(Lecture.class).fetch("attendent").where().eq("email",email).findList();
    }

    public static Lecture getlecturebyname(String name,String databasename){
        return getServer(databasename).find(Lecture.class).where().eq("courseName",name).findUnique();
    }

//    @Override
//    public void save(String semester){
//        Ebean.getServer(semester).save(this);
//
//    }
//    @Override
//    public void delete(String semester){
//        Ebean.getServer(semester).delete(this);
//    }
}
