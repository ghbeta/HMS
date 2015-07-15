package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hao on 2015/7/6.
 */
@Entity
@Table(name = "Courses")
public class Course extends Model {
    @Id
    public String courseId;

    @OneToOne
    public User Creator;



    @OneToMany
    public List<User> Assistant = new ArrayList<>();

    public String courseName;


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public User getCreator() {
        return Creator;
    }

    public void setCreator(User creator) {
        Creator = creator;
    }

    public List<User> getAssistant() {
        return Assistant;
    }

    public void setAssistant(List<User> assistant) {
        Assistant = assistant;
    }

    @Override
    public void save(String semester){
        Ebean.getServer(semester).save(this);

    }
}
