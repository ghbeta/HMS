package models;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
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

    public int totalpoints;

    public String uploadfile;

    public String filename;

    @Column(columnDefinition = "TEXT")
    public String comments;

    @Column(columnDefinition = "TEXT")
    public String addtionalinfo;

    public boolean ishandin;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date deadline;

    public Date handin;

    public String semester;

    @Version
    private Long version;

    @ManyToMany(cascade= CascadeType.ALL)
    public List<Semesteruser> students;
//
    @OneToMany(cascade= CascadeType.ALL,mappedBy = "assignment")
    public List<Exercise> exercises;

    @ManyToOne(cascade= CascadeType.ALL)
    //@JoinColumn
    public Lecture lecture;

    public boolean isExpired(){return deadline!=null&&handin.before(deadline);}

    public int getNumberofHandinStudents(Assignment assignment,Lecture lecture,String database){
        return Semesteruser.findByAssignemtnandLecture(assignment,lecture,database).size();
    }
//
    public static Assignment findById(String database,String id){
        return getServer(database).find(Assignment.class).where().eq("id",id).findUnique();
    }

}
