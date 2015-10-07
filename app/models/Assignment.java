package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Hao on 2015/10/7.
 */
@Entity
@Table(name="Assignment")
public class Assignment extends Model {
    @Id
    public String title;

    public int numberofexercise;

    public int totalpoints;



    @ManyToOne
    @JoinColumn(name="assignments")
    public User editor;

    @OneToMany(mappedBy = "assignment")
    public Set<Exercise> exercises;
}
