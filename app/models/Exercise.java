package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by Hao on 2015/10/7.
 */
@Entity
@Table(name="exercise")
public class Exercise extends Model {
    @Id
    public String title;

    public String semester;

    public boolean evalueated;

    public boolean ishandin;

    @Column(columnDefinition = "TEXT")
    public String content;

    @ManyToOne
    @JoinColumn(name="exercises")
    public Semesteruser marker;

    @ManyToOne
    @JoinColumn(name="exercises")
    public Assignment assignment;
}
