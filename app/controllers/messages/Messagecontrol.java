package controllers.messages;

import Permission.Securedstudents;
import models.*;
import play.Logger;
import play.api.libs.json.Json;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.WebSocket;

/**
 * Created by Hao on 2015/11/14.
 */
public class Messagecontrol extends Controller {

  @Security.Authenticated(Securedstudents.class)
  public static Result generateMessagehome(){
      User user=User.findByEmail(ctx().session().get("email"),"global");
      return ok(views.html.messagesystem.messagecontainer.render(user));
  }





  @Security.Authenticated(Securedstudents.class)
  public static Result addConversation(String semester,String user2,String lecturename){
      Semesteruser u1=Semesteruser.findByEmail(ctx().session().get("email"),semester);//always message creator
      Semesteruser u2=Semesteruser.findByEmail(user2,semester);
      Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
      DynamicForm messageform= Form.form().bindFromRequest();
      String content=messageform.get("content");
      Message message=new Message();
      message.setTimestamp();
      message.semester=semester;
      message.sender=u1;
      message.messagebody=content;
      message.save(semester);
      Conversation conversation=Conversation.getConversation(semester,u1,u2);
      if(conversation==null){
      try{
      conversation=new Conversation();
      //conversation.lecture=lecture;
      conversation.user1=u1;
      conversation.user2=u2;
      //message.conversation=conversation;
      //message.update(semester);
     // message.lecture=lecture;
      //message.save(semester);
      conversation.messages.add(message);
      //message.update(semester);
      conversation.save(semester);
      message.conversation=conversation;
      message.update(semester);
          flash("success", Messages.get("messages.send"));
          if(u1.roles.equals(UserRoll.Teachers.toString())||u1.roles.equals(UserRoll.Assistants.toString())){
              return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(u1.lastname, semester, lecture.courseName));}
          else{
              return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(u1.lastname, semester, lecture.courseName));
          }}
      catch(Exception e){
          Logger.warn(e.getMessage());
          flash("danger", Messages.get("message.sendfail"));
          if(u1.roles.equals(UserRoll.Teachers.toString())||u1.roles.equals(UserRoll.Assistants.toString())){
          return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(u1.lastname, semester, lecture.courseName));}
          else{
              return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(u1.lastname, semester, lecture.courseName));
          }
      }}
      else{
          message.conversation=conversation;
          conversation.messages.add(message);
          message.update(semester);
          conversation.update(semester);
          flash("success",Messages.get("messages.send"));
          if(u1.roles.equals(UserRoll.Teachers.toString())||u1.roles.equals(UserRoll.Assistants.toString())){
              return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(u1.lastname, semester, lecture.courseName));}
          else{
              return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(u1.lastname, semester, lecture.courseName));
          }
      }
  }
}
