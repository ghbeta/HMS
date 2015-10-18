package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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



    public boolean ishandin;

    public String semester;

    @Version
    private Long version;

    @ManyToMany
    public List<Semesteruser> students;
//
    @OneToMany(mappedBy = "assignment")
    public List<Exercise> exercises;

    @ManyToOne
    //@JoinColumn
    public Lecture lecture;
//
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "assignment")
    public List<Message> messages;
}
