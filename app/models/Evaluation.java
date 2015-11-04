package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import static com.avaje.ebean.Ebean.getServer;
/**
 * Created by Hao on 2015/11/4.
 */
@Entity
@Table(name = "evaluation")
public class Evaluation extends Model {

    @Id
    @GeneratedValue
    public String id;

    @ManyToOne
    public Lecture lecture;

    @ManyToOne
    public Semesteruser student;

//    @OneToMany(cascade = CascadeType.ALL,mappedBy = "evaluation")
//    List<Handin> handins;

    public float performance;

    public void setPerformance(String semester,Lecture lecture,Semesteruser semesteruser){
        List<Handin> optional =Handin.getOptionalAssignmentofStudentsinLecture(semester,lecture,semesteruser);
        List<Handin> required= Handin.getValidHandinofstudentinlecture(lecture,semesteruser,semester);

        float totalpoints=0f;
        float getpoints=0f;
        for(int i=0;i<lecture.assignments.size();i++){
            totalpoints=totalpoints+lecture.assignments.get(i).totalpoints;
        }

        for(int i =0;i<optional.size();i++){
            getpoints=getpoints+optional.get(i).earndpoints;
        }

        for(int i=0;i<required.size();i++){
            getpoints=getpoints+required.get(i).earndpoints;
        }

        performance=getpoints/totalpoints;
    }

    public static Evaluation findByLectureAndUser(String semester,Lecture lecture,Semesteruser semesteruser){
        return getServer(semester).find(Evaluation.class).where().eq("lecture.courseName",lecture.courseName).eq("student.email",semesteruser.email).findUnique();
    }
}
