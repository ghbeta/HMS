package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionFactory;
import play.Logger;
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

    public String semester;

//    @ManyToOne
//    public Lecture lecture;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "conversation")
    public List<Message> messages;


    public static Conversation getConversation(String semester,Semesteruser user1,Semesteruser user2){
        ExpressionFactory expr=Ebean.getServer(semester).getExpressionFactory();
        return getServer(semester).find(Conversation.class).where().or(expr.and(expr.eq("user1.email", user1.email), expr.eq("user2.email", user2.email)),expr.and(expr.eq("user1.email", user2.email), expr.eq("user2.email", user1.email))).findUnique();
        //return getServer(semester).find(Conversation.class).where().eq("user1.email",user1.email).findUnique();
    }

    public static List<Conversation> getConversationByOneuser(String semester,Semesteruser user){
        ExpressionFactory expr=Ebean.getServer(semester).getExpressionFactory();
        return getServer(semester).find(Conversation.class).fetch("user1").fetch("user2").where().or(expr.eq("user1.email",user.email),expr.eq("user2.email",user.email)).findList();
    }

    public static Conversation getConversationById(String semester,String convid){
        return getServer(semester).find(Conversation.class).where().eq("id",convid).findUnique();
    }
}
