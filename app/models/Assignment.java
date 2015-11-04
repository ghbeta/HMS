package models;

import org.apache.commons.io.FileUtils;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.avaje.ebean.Ebean.getServer;

/**
 * Created by Hao on 2015/10/7.
 */
@Entity
@Table(name="assignment")
public class Assignment extends Model {
    @Id
    @GeneratedValue
    public String id;

    public String title;

    public int numberofexercise;

    public float totalpoints;



    public String uploadfile;

    public String filename;



    @Column(columnDefinition = "TEXT")
    public String addtionalinfo;



    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date deadline;




    public String semester;

    public boolean isoptional;

    @Version
    private Long version;

//    @ManyToMany(cascade= CascadeType.ALL,mappedBy = "assignments")
//    public List<Semesteruser> students;
//
    @OneToMany(cascade= CascadeType.ALL,mappedBy = "assignment")
    public List<Exercise> exercises;

    @ManyToOne//(cascade= CascadeType.REMOVE)
    //@JoinColumn
    public Lecture lecture;

    public boolean isExpired(){
        Date date=new Date();
        return deadline!=null&&date.before(deadline);}









    public List<Assignment> getHandinAssignmentofLecture(Assignment assignment,Lecture lecture,String database){

        return getServer(database).find(Assignment.class).where().eq("id",assignment.id).eq("ishandin",true).eq("lecture.courseName",lecture.courseName).findList();
    }

    public static List<Assignment> getValidHandinOfStudentsinLecture(String database,Lecture lecture,Semesteruser semesteruser){
        List<Assignment> allvalidhandin= getServer(database).find(Assignment.class).where().eq("isvalid",true).eq("lecture.courseName",lecture.courseName).findList();
        Iterator<Assignment> iterator=allvalidhandin.iterator();
        while(iterator.hasNext()){
            if(!iterator.next().students.contains(semesteruser)){
               iterator.remove();
            }
        }
        return allvalidhandin;
    }

    public static List<Assignment> getOptionalAssignmentofStudentsinLecture(String database,Lecture lecture,Semesteruser semesteruser){
        List<Assignment> alloptionalhandin= getServer(database).find(Assignment.class).where().eq("isoptional",true).eq("lecture.courseName",lecture.courseName).findList();
        Iterator<Assignment> iterator=alloptionalhandin.iterator();
        while(iterator.hasNext()){
            if(!iterator.next().students.contains(semesteruser)){
                iterator.remove();
            }
        }
        return alloptionalhandin;
    }
//
    public static Assignment findById(String database,String id){
        return getServer(database).find(Assignment.class).where().eq("id",id).findUnique();
    }

    public static Assignment findByLectureAndName(String database,String lecturename,String assignmentname){
        return getServer(database).find(Assignment.class).where().eq("lecture.courseName",lecturename).where().eq("title",assignmentname).findUnique();
    }

    public static void deleteAssignment(String database,Assignment assignment){
        try{
            FileUtils.forceDelete(new File("files/" + assignment.uploadfile));
            assignment.delete(database);
        }catch(Exception e){}
        assignment.delete(database);
    }
}
