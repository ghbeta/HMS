package models;

import com.avaje.ebean.Ebean;
import org.joda.time.DateTime;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Hao on 2015/7/6.
 */
@Entity
@Table(name = "Lecture")
public class Lecture extends Model {
    @Id
    @Column(unique = true)
    public String courseName;

    @Column(columnDefinition = "TEXT")
    public String desription;


    public DateTime closingdate;

    public int optionalDuties;
    public float lowerProcentualBounderyOfDuties;

    public float minimumPercentageForExamination;

    public String semester;

    @OneToOne
    public Semesteruser lasteditor;



    @ManyToMany(cascade=CascadeType.ALL)
    public List<Semesteruser> assistants;

    @OneToMany(cascade=CascadeType.ALL,mappedBy = "course")
    public Set<Repo> repos;



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
