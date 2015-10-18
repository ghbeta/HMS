package models;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Hao on 2015/10/9.
 */
@Entity
@Table(name="messages")
public class Message extends Model {
    @Id
    @GeneratedValue
    String messageid;
    @Column(columnDefinition = "Text")
    public String messagebody;

    @Version
    private Long version;

    public String semester;

    @Formats.DateTime(pattern = "dd.MM.yyyy hh:mm:ss")
    public org.joda.time.DateTime date;

    @ManyToOne(cascade= CascadeType.ALL)
    public Message parent;
    @OneToMany(mappedBy = "parent")
    public List<Message> answers;





    @ManyToOne(cascade= CascadeType.ALL)
    public Lecture lecture;
//

//
    @ManyToOne(cascade= CascadeType.ALL)
    public Semesteruser sender;

}
