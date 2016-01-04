package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.List;

import static com.avaje.ebean.Ebean.getServer;

/**
 * Created by Hao on 2015/10/9.
 */
@Entity
@Table(name="semesters")
public class Semester extends Model {
    @Id
    public String semester;

    public String getSemester() {
        return semester;
    }
//
//    public void setSemester(String semester) {
//        this.semester = semester;
//    }

    public static Semester findsemester(String semester){
        return getServer("global").find(Semester.class).where().eq("semester",semester).findUnique();
    }

    public static List<Semester> getallsemester(){
        return getServer("global").find(Semester.class).orderBy("semester desc").findList();
    }
}
