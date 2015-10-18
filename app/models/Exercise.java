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

    public float points;

    public boolean evalueated;

    @ManyToMany
    public List<Semesteruser> students;

    @ManyToOne
    //@JoinColumn(name="exercises")
    public Semesteruser marker;

    @ManyToOne
    //@JoinColumn(name="exercises")
    public Assignment assignment;

    @OneToMany(mappedBy = "exercise")
    public List<Message> messages;
}
