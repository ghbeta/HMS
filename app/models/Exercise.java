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

    @Column(columnDefinition = "TEXT")
    public String content;

    @ManyToOne
    @JoinColumn(name="user_id")
    public User marker;

    @ManyToOne
    @JoinColumn(name="assignment_id")
    public Assignment assignment;
}
