package models;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Hao on 2015/10/9.
 */
@Entity
@Table(name="messages")
public class Message extends Model {
    @Column(columnDefinition = "Text")
    public String messagebody;

    public String semester;

    @Formats.DateTime(pattern = "dd.MM.yyyy hh:mm:ss")
    private org.joda.time.DateTime date;

    @OneToMany(mappedBy = "parent")
    private final Set<Message> answers = new LinkedHashSet<Message>();

    @ManyToOne(cascade = CascadeType.ALL)
    private Message parent;

    @ManyToOne(cascade = CascadeType.ALL)
    private Exercise exercise;

    @ManyToOne(cascade = CascadeType.ALL)
    private Lecture lecture;

    @ManyToOne(cascade = CascadeType.ALL)
    private Assignment assignment;

    @ManyToOne(cascade = CascadeType.ALL)
    private Semesteruser sender;

}
