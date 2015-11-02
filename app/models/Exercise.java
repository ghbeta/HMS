package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Hao on 2015/10/7.
 */
@Entity
@Table(name="exercise")
public class Exercise extends Model {
    @Id
    @GeneratedValue
    public String id;

    public String title;

    public String semester;

    public float totalpoints;
    public float earndpoints;

    public boolean evaluated;

    @Column(columnDefinition = "TEXT")
    public String comments;

    @ManyToMany(cascade= CascadeType.ALL)
    public List<Semesteruser> students;

    @ManyToOne//(cascade= CascadeType.ALL)
    //@JoinColumn(name="exercises")
    public Semesteruser marker;

    @ManyToOne//(cascade= CascadeType.ALL)
    //@JoinColumn(name="exercises")
    public Assignment assignment;


}
