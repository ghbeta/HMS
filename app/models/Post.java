package models;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hao on 2015/11/10.
 */
@Entity
@Table(name="post")
public class Post extends Model {
    @Id
    @GeneratedValue
    public String id;

    @Column(columnDefinition = "TEXT")
    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date creattime;

    @ManyToOne
    public Thread parent;

    @ManyToOne
    public Semesteruser creator;

    @ManyToOne
    public Lecture lecture;

}
