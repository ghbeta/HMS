package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by Hao on 2015/7/6.
 */
@Entity
public class Course extends Model {
    @Id
    public String courseId;

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

    public String courseName;

    @OneToOne
    public User Creator;

    @Override
    public void save(String semester){
        Ebean.getServer(semester).save(this);

    }
}
