package models;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date closingdate;

    public boolean closed;
    public int totalassignment;
    public int optionalassignments;
    public int requriednumberofvalidassignment;
    public float requiredpercentfovalidassignment;

    public float minimumPercentageForExamination;

    //public float performance;
    public String semester;

    @OneToOne
    public Semesteruser lasteditor;
//
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "lecture")
    @OrderColumn(name = "title")
    public List<Assignment> assignments;

    @OneToMany(cascade = CascadeType.ALL,mappedBy="lecture")
    public List<Handin> handins;

    @ManyToMany(cascade=CascadeType.ALL,mappedBy = "lectures")
    public List<Semesteruser> attendent;
//
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "course")
    public List<Repo> repos;
//
//    @OneToMany(cascade=CascadeType.ALL,mappedBy = "lecture")
//    public List<Message> messages;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "lecture")
    public List<Evaluation> evaluations;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "lecture")
    public List<ForumThread> threads;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "lecture")
    public List<ForumPost> posts;

//    @OneToMany(cascade = CascadeType.ALL,mappedBy = "lecture")
//    public List<Conversation> conversations;

    public boolean isExpired(){
        Date date=new Date();
        return closingdate!=null&&date.before(closingdate);
    }

//    public boolean isUsercontain(Semesteruser user){
//        if(user==null){
//            return false;
//        }
//        else{
//            return attendent.contains(user);
//        }
//    }


//    public void setPerformance(String semester,Lecture lecture,Semesteruser semesteruser){
//        List<Assignment> optional =Assignment.getOptionalAssignmentofStudentsinLecture(semester,lecture,semesteruser);
//        List<Assignment> required= Assignment.getValidHandinOfStudentsinLecture(semester,lecture,semesteruser);
//        float totalpoints=0f;
//        float getpoints=0f;
//        for(int i=0;i<assignments.size();i++){
//            totalpoints=totalpoints+assignments.get(i).totalpoints;
//        }
//
//        for(int i =0;i<optional.size();i++){
//            getpoints=getpoints+optional.get(i).gettotalearndpoint();
//        }
//
//        for(int i=0;i<required.size();i++){
//            getpoints=getpoints+required.get(i).gettotalearndpoint();
//        }
//
//        performance=getpoints/totalpoints;
//    }

    public static boolean addSemesterusertoLecture(String database, Semesteruser user, Lecture currentlecture){

         if(!currentlecture.attendent.contains(user)){
             currentlecture.attendent.add(user);
             currentlecture.update(database);
             return true;
         }
else{
             return false;
         }
    }

    public static boolean deleteSemesteruserfromLecture(String database, Semesteruser user, Lecture currentlecture){
        if(currentlecture.attendent.contains(user)){
            currentlecture.attendent.remove(user);
            //user.assignments.removeAll(currentlecture.assignments);
            user.handins.removeAll(Handin.getAllHandinofStudentsinLecture(database,currentlecture,user));
            user.evaluations.remove(Evaluation.findByLectureAndUser(database,currentlecture,user));
            user.update(database);
            currentlecture.update(database);
            return true;
        }
        else{
            return false;
        }
    }

    public static List<Lecture> getalllectures(String email,String databasename){

         Semesteruser currentuser;//[]= new Semesteruser[1];
        if(Semesteruser.findByEmail(email,databasename)!=null) {
            currentuser = Semesteruser.findByEmail(email, databasename);
            List<Lecture> all=getServer(databasename).find(Lecture.class).findList();
            if(currentuser.lectures!=null&&all.removeAll(currentuser.lectures)){

            return all;

        }
            else {return all;}
        }
        else{
            return getServer(databasename).find(Lecture.class).findList();
        }
    }

    public static List<Lecture> getalllecturesbyemail(String email,String databasename){
        return getServer(databasename).find(Lecture.class).where().eq("attendent.email", email).findList();// retrun only the lecture list with the username
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
