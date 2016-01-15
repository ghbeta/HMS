package models;

import play.db.ebean.Model;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import static com.avaje.ebean.Ebean.getServer;
/**
 * Created by Hao on 2015/11/4.
 */
@Entity
@Table(name = "handins")
public class Handin extends Model {
    @Id
    @GeneratedValue
    public String id;

    @ManyToOne
    public Semesteruser student;

    @ManyToOne
    public Semesteruser marker;

    @ManyToOne
    public Lecture lecture;

    @ManyToOne
    public Assignment assignment;

//    @ManyToOne
//    Evaluation evaluation;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "handin")
    public List<Exercise> exercises;

    @Version
    private Long version;

    public float totalpoints;

    public float earndpoints;

    public Date handin;

    public boolean ishandin;

    public boolean isvalid;

    public boolean isevaluated;

    public String comments;

    public void setishandin(){
        ishandin=(assignment.deadline !=null&&handin.before(assignment.deadline));
    }

//    public boolean getishandin(){
//        return ishandin;
//    }

    public void setIsvalid(){
        float percentage=earndpoints/assignment.totalpoints;
        isvalid= (percentage>=lecture.requiredpercentfovalidassignment);
    }

        public void setTotalpoints(){
        totalpoints=assignment.totalpoints;
//        for(int i=0;i<exercises.size();i++){
//            totalpoints=totalpoints+exercises.get(i).totalpoints;
//        }
    }

    public void setEarndpoints(float assignmentearndpoints){
        earndpoints=assignmentearndpoints;
//        for(int i=0;i<exercises.size();i++){
//            earndpoints=earndpoints+exercises.get(i).earndpoints;
//        }
    }

    public static List<Handin> getHandinofassignmentinlecture(Assignment assignment,Lecture lecture,String semester){
        return getServer(semester).find(Handin.class).where().eq("assignment.id",assignment.id).eq("lecture.courseName",lecture.courseName).findList();
    }

    public static List<Handin> getValidHandinofstudentinlecture(Lecture lecture,Semesteruser semesteruser,String semester){
        return getServer(semester).find(Handin.class).where().eq("isvalid",true).eq("student.email",semesteruser.email).eq("lecture.courseName",lecture.courseName).findList();
    }

    public static List<Handin> getOptionalAssignmentofStudentsinLecture(String database,Lecture lecture,Semesteruser semesteruser){
        return getServer(database).find(Handin.class).where().
                eq("assignment.isoptional", true).
                eq("student.email", semesteruser.email).
                eq("lecture.courseName", lecture.courseName).findList();
    }

    public static List<Handin> getAllHandinofStudentsinLecture(String database,Lecture lecture,Semesteruser semesteruser){
        return getServer(database).find(Handin.class).where().eq("student.email", semesteruser.email).
                eq("lecture.courseName", lecture.courseName).findList();
    }

    public static Handin getHandinofassignmentofstudentinlecture(String database,Lecture lecture,Semesteruser semesteruser,Assignment assignment){
        return getServer(database).find(Handin.class).where().eq("student.email",semesteruser.email)
                .eq("lecture.courseName",lecture.courseName)
                .eq("assignment.id",assignment.id).findUnique();
    }

}
