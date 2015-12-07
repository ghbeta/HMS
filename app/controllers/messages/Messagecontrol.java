package controllers.messages;

import Permission.Securedstudents;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.avaje.ebean.text.json.JsonWriteOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;
import utils.Chatsocket;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by Hao on 2015/11/14.
 */
public class Messagecontrol extends Controller {

  @Security.Authenticated(Securedstudents.class)
  public static Result generateMessagehome(){
      User user=User.findByEmail(ctx().session().get("email"),"global");
      return ok(views.html.messagesystem.messagecontainer.render(user));
  }


//  @Security.Authenticated(Securedstudents.class)
//  @BodyParser.Of(BodyParser.Json.class)
//  public static Result semesterrequest(String semester) throws JsonProcessingException {
//      Logger.warn("semester is "+semester);
//      Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
//      if(currentuser!=null){
//      List<Conversation> conversations=Conversation.getConversationByOneuser(semester,currentuser);
//          JsonContext json= Ebean.getServer(semester).createJsonContext();
//          String jsonoutput=json.toJsonString(conversations);
//          Logger.warn(jsonoutput);
//      return ok(jsonoutput);}
//      else{
//          return badRequest();
//      }
//
//  }

//    @Security.Authenticated(Securedstudents.class)
//    @BodyParser.Of(BodyParser.Json.class)
//    public static Result contentrequest(String semester,String conversationid){
//         Conversation conversation=Conversation.getConversationById(semester, conversationid);
//         List<Message> messages=Message.findAllByConversation(semester,conversation);
//        if(messages!=null){
//        JsonContext json = Ebean.getServer(semester).createJsonContext();
//        String jsonoutput=json.toJsonString(messages,true);
//        Logger.warn("contentrequest "+jsonoutput);
//        return ok(jsonoutput);}
//        else{
//            return badRequest();
//        }
//
//    }

//    @Security.Authenticated(Securedstudents.class)
//    @BodyParser.Of(BodyParser.Json.class)
//    public static Result addmessage(String semester,String conversationid){
//        Conversation conversation=Conversation.getConversationById(semester,conversationid);
//        Semesteruser semesteruser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
//        JsonNode json = request().body().asJson();
//        String msgcontent=json.findPath("content").textValue();
//        if(conversation!=null) {
//            Message message = new Message();
//            message.conversation=conversation;
//            message.date=new Date();
//            message.messagebody=msgcontent;
//            message.semester=semester;
//            message.sender=semesteruser;
//            message.setTimestamp();
//            message.save(semester);
//            conversation.messages.add(message);
//            conversation.update(semester);
//            List<Message> allmessages=Message.findAllByConversation(semester,conversation);
//            JsonContext jsonresult= Ebean.getServer(semester).createJsonContext();
//            String jsonoutput=jsonresult.toJsonString(allmessages,true);
//            Logger.warn("after adding message "+jsonoutput);
//            return ok(jsonoutput);
//        }else{
//            return badRequest();
//        }
//
//    }


//    @Security.Authenticated(Securedstudents.class)
//    @BodyParser.Of(BodyParser.Json.class)
//    public static Result usernamerequest(String email,String semester){
//        Semesteruser user=Semesteruser.findByEmail(email,semester);
//        if(user!=null){
//            //List<Conversation> conversations=Conversation.getConversationByOneuser(semester,currentuser);
//            JsonContext json= Ebean.getServer(semester).createJsonContext();
//            String jsonoutput=json.toJsonString(user);
//            Logger.warn(jsonoutput);
//            return ok(jsonoutput);}
//        else{
//            return badRequest();
//        }
//    }


  @Security.Authenticated(Securedstudents.class)
  @BodyParser.Of(BodyParser.Json.class)
  public static WebSocket<String> socket(){
      User currentuser=User.findByEmail(ctx().session().get("email"),"global");
      if(currentuser!=null) {
          return WebSocket.whenReady((in, out) -> {

              Chatsocket.start(currentuser.email, in, out);
          });
      }
      else
      {
          return WebSocket.whenReady((in,out)->{
              ObjectNode result = Json.newObject();
              result.put("event","connect to notification system");
              out.write(result.toString());
          });
      }
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
      conversation.semester=semester;
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
