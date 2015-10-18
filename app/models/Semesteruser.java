package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Assignment> assignments;

    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "students")
    public List<Exercise> handinexercises;
//
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "owner")
    public List<Repo> repos;
//
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "marker")
    public List<Exercise> exercises;
//
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sender")
    public List<Message> messages;

    public static Semesteruser findByEmail(String email,String database) {
        return currentServer(database).find(Semesteruser.class).where().eq("email", email).findUnique();
    }

    public static Semesteruser findById(String id,String database){
        return currentServer(database).find(Semesteruser.class).where().eq("id",id).findUnique();
    }

    public static List<Semesteruser> findStudentsByLecture(Lecture lecture,String database){
        return currentServer(database).find(Semesteruser.class).fetch("lectures").where().eq("courseName",lecture.courseName).where().eq("roles",UserRoll.Students.toString()).findList();
    }


}
