package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static com.avaje.ebean.Ebean.getServer;

/**
 * Created by Hao on 2015/11/10.
 */
@Entity
@Table(name = "thread")
public class ForumThread extends Model {
    @Id
    @GeneratedValue
    public String id;

    @Column(columnDefinition = "TEXT")
    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date creattime;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date lastupdate;


    public void setLastupdatetimestamp() {
        this.lastupdatetimestamp = System.currentTimeMillis();
    }

    public Long lastupdatetimestamp;

    @Version
    private Long version;

    @ManyToOne
    public Lecture lecture;

    @ManyToOne
    public Semesteruser creator;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "parent")
    public List<ForumPost> replyposts;

    public static List<ForumThread> findByLecture(String semester,Lecture lecture){
        return getServer(semester).find(ForumThread.class).where().eq("lecture.courseName", lecture.courseName).orderBy("lastupdatetimestamp,lastupdatetimestamp desc").findList();
    }

    public static ForumThread findById(String semester,String id){
        return getServer(semester).find(ForumThread.class).where().eq("id",id).findUnique();
    }

    public static List<ForumThread> findByLectureByStudent(String semester,Lecture lecture,Semesteruser semesteruser){
        return getServer(semester).find(ForumThread.class).where().eq("lecture.courseName",lecture.courseName).eq("creator.id",semesteruser.id).orderBy("lastupdatetimestamp,lastupdatetimestamp desc").findList();
    }
}
