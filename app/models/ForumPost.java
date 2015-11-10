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
public class ForumPost extends Model {
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
    public ForumThread parent;

    @Version
    private Long version;

    @ManyToOne
    public Semesteruser creator;

    @ManyToOne
    public Lecture lecture;

}
