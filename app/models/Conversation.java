package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

import static com.avaje.ebean.Ebean.getServer;

/**
 * Created by Hao on 2015/11/14.
 */
@Entity
@Table(name = "conversation")
public class Conversation extends Model {
    @Id
    @GeneratedValue
    String id;

    @Version
    private Long version;

    @OneToOne
    public Semesteruser user1;

    @OneToOne
    public Semesteruser user2;

//    @ManyToOne
//    public Lecture lecture;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "conversation")
    public List<Message> messages;


    public static Conversation getConversation(String semester,Semesteruser user1,Semesteruser user2){
        return getServer(semester).find(Conversation.class).where().or(Expr.and(Expr.eq("user1.email", user1.email), Expr.eq("user2.email", user2.email)),
                Expr.and(Expr.eq("user1.email", user2.email), Expr.eq("user2.email", user1.email))).findUnique();
    }

}
