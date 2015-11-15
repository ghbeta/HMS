package models;

import org.joda.time.DateTime;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
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
    public Date date;

    public void setTimestamp() {
        this.date=new Date();
        this.timestamp = System.currentTimeMillis();
    }

    public Long timestamp;


//    @ManyToOne//(cascade= CascadeType.ALL)
//    public Message parent;
//    @OneToMany(cascade= CascadeType.ALL,mappedBy = "parent")
//    public List<Message> answers;


    @ManyToOne
    public Conversation conversation;



//    @ManyToOne//(cascade= CascadeType.ALL)
//    public Lecture lecture;
//

//
    @ManyToOne//(cascade= CascadeType.ALL)
    public Semesteruser sender;

}
