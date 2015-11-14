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
  public static Result addConversation(String semester,String user2,String lecturename){
      Semesteruser u1=Semesteruser.findByEmail(ctx().session().get("email"),semester);//always message creator
      Semesteruser u2=Semesteruser.findByEmail(user2,semester);
      Lecture lecture=Lecture.getlecturebyname(lecturename,semester);
      DynamicForm messageform= Form.form().bindFromRequest();
      String content=messageform.get("content");
      try{
      Message message=new Message();
      Conversation conversation=new Conversation();
      conversation.lecture=lecture;
      conversation.user1=u1;
      conversation.user2=u2;
      message.setTimestamp();
      message.semester=semester;
      message.lecture=lecture;
      message.sender=u1;
      message.conversation=conversation;
      message.messagebody=content;
      //message.save(semester);
      conversation.messages.add(message);
      conversation.save(semester);
          flash("success",Messages.get("messages.send"));
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
      }
  }
}
