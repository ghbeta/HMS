package models;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Version
    private Long version;

    @ManyToOne
    public Lecture lecture;

    @ManyToOne
    public Semesteruser creator;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "parent")
    public List<ForumPost> replyposts;
}
