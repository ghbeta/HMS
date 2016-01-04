package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Hao on 2015/10/8.
 */
@Entity
@Table(name="semesterusers")
public class Semesteruser extends Abstractuser {

    @Constraints.Required
    @Formats.NonEmpty
    public String semester;

    @ManyToMany(cascade= CascadeType.ALL)
    public List<Lecture> lectures;
//
//    @ManyToMany(cascade = CascadeType.ALL)
//    public List<Assignment> assignments;
    @OneToMany(cascade = CascadeType.ALL,mappedBy="student")
    public List<Handin> handins;

//    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "students")
//    public List<Exercise> handinexercises;
//
    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "owner")
    public List<Repo> repos;
//
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "marker")
    public List<Handin> correctedhandins;
//
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sender")
    public List<Message> messages;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "student")
    public List<Evaluation> evaluations;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "creator")
    public List<ForumThread> threads;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "creator")
    public List<ForumPost> posts;


    public Repo getRepoByLecture(Lecture lecture){
        Repo repo=Repo.findRepoByLectureAndOwner(lecture.semester,this,lecture);
        return repo;
    }


    public static Semesteruser getSemesteruserfomrUser(String database,User user){
        Semesteruser semesteruser = null;
        try {
            semesteruser = Semesteruser.findByEmail(user.email, database);
        } catch (Exception e) {
            semesteruser = null;
        }


        if (semesteruser == null) {
            semesteruser = new Semesteruser();
            semesteruser.email = user.email;
            semesteruser.firstname = user.firstname;
            semesteruser.id = user.id;
            semesteruser.lastname = user.lastname;
            semesteruser.roles = user.roles;
            semesteruser.userHash=user.userHash;
            //semesteruser.sshs = user.sshs;
            //suser=globaluser;
            semesteruser.semester = database;
            semesteruser.save(database);
            return semesteruser;
        }
        else{
            return semesteruser;
        }


    }

//    public Repo getRepofromRepos(String semester,Semesteruser semesteruser,Lecture lecture){
//        return Repo.findRepoByLectureAndOwner(semester,semesteruser,lecture);
//    }

    public static Semesteruser findByEmail(String email,String database) {
        return currentServer(database).find(Semesteruser.class).where().eq("email", email).findUnique();
    }

//    public static Semesteruser findByUserhash(String userhash,String database){
//        return currentServer(database).find(Semesteruser.class).where().eq("userHash", userhash).findUnique();
//    }

//    public static Semesteruser findById(String id,String database){
//        return currentServer(database).find(Semesteruser.class).where().eq("id",id).findUnique();
//    }

//    public static List<Semesteruser> findByAssignemtnandLecture(Assignment assignment,Lecture lecture,String database){
//        return currentServer(database).find(Semesteruser.class).fetch("lectures").fetch("assignments").where().eq("lectures.id",lecture.courseName).eq("assignments.id",assignment.id).findList();
//    }

//    public static List<Semesteruser> findSemesteruserbyLecture(String database,Lecture lecture){
//        return currentServer(database).find(Semesteruser.class).fetch("lectures").where().eq("courseName",lecture.courseName).findList();
//    }

    public static List<Semesteruser> findAllstudentsByLecture(String database,Lecture lecture){
        return currentServer(database).find(Semesteruser.class).where().eq("lectures.courseName", lecture.courseName).eq("roles", UserRoll.Students.toString()).findList();
    }
//    public List<Semesteruser> getstudents(List<Semesteruser> all){
//        Iterator<Semesteruser> s=all.iterator();
//        while(s.hasNext()){
//            Semesteruser current=s.next();
//            if(!current.roles.equals(UserRoll.Students.toString())){
//                s.remove();
//            }
//        }
//        return all;
//        //return currentServer(database).find(Semesteruser.class).fetch("lectures").where().eq("courseName",lecture.courseName).where().eq("roles",UserRoll.Students.toString()).findList();
//    }

    public static List<Semesteruser> findworkerByLecture(String database,Lecture lecture){
        return currentServer(database).find(Semesteruser.class).where().eq("lectures.courseName",lecture.courseName).ne("roles",UserRoll.Students.toString()).findList();
    }

}
