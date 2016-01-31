package utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Conversation;
import models.Message;
import models.Semesteruser;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hao on 2015/11/16.
 */
public class Chatsocket {

    public static HashMap<String,WebSocket.Out<String>> connections = new HashMap<String, WebSocket.Out<String>>();
    public static void start(String useremail,WebSocket.In<String> in, WebSocket.Out<String> out){
        connections.put(useremail,out);
        //connections.put(userid2,out);
        in.onMessage(new F.Callback<String>() {
            @Override
            public void invoke(String event) throws Throwable {
                JsonNode inmsg = Json.parse(event);
                Logger.warn(event);
                if(inmsg.findPath("event").asText().equals("allconversations")){

                    JsonNode requestbody=inmsg.findPath("data");
                    //Logger.warn(requestbody.findPath("semester").asText());
                    String semester=requestbody.findPath("semester").asText();
                    //Logger.warn(requestbody.findPath("email").asText());
                    String email=requestbody.findPath("email").asText();
                    Semesteruser currentuser=Semesteruser.findByEmail(email,semester);
                    if(currentuser!=null){
                        List<Conversation> conversations=Conversation.getConversationByOneuser(semester,currentuser);
                        JsonContext json= Ebean.getServer(semester).createJsonContext();
                        String jsonoutput=json.toJsonString(conversations);
                        ObjectNode result = Json.newObject();
                        result.put("event","allconversations");
                        result.put("data",jsonoutput);
                        //Logger.warn(result.toString());
                        //return ok(jsonoutput);
                        out.write(result.toString());
                        }
                }

                if(inmsg.findPath("event").asText().equals("chatcontent")){
                    JsonNode requestbody=inmsg.findPath("data");
                    String conversationid=requestbody.findPath("convid").asText();
                    String semester=requestbody.findPath("semester").asText();
                    //Logger.warn(requestbody.asText());
                    Conversation conversation=Conversation.getConversationById(semester, conversationid);
                    List<Message> messages=Message.findAllByConversation(semester,conversation);
                    if(messages!=null){
                        JsonContext json = Ebean.getServer(semester).createJsonContext();
                        String jsonoutput=json.toJsonString(messages,true);
                        ObjectNode result=Json.newObject();
                        result.put("event","chatcontent");
                        result.put("data",jsonoutput);
                        Logger.warn("contentrequest " + jsonoutput);
                        out.write(result.toString());
                    }
                }

                if(inmsg.findPath("event").asText().equals("newmessage")){
                    JsonNode requestbody=inmsg.findPath("data");
                    String conversationid=requestbody.findPath("convid").asText();
                    String semester=requestbody.findPath("semester").asText();
                    String msgcontent=requestbody.findPath("content").asText();
                    String other=requestbody.findPath("other").asText();
                    Conversation conversation=Conversation.getConversationById(semester,conversationid);
                    Semesteruser semesteruser=Semesteruser.findByEmail(useremail,semester);
                    if(conversation!=null) {
                        Message message = new Message();
                        message.conversation=conversation;
                        message.date=new Date();
                        message.messagebody=msgcontent;
                        message.semester=semester;
                        message.sender=semesteruser;
                        message.setTimestamp();
                        message.save(semester);
                        conversation.messages.add(message);
                        conversation.update(semester);
                        List<Message> allmessages=Message.findAllByConversation(semester,conversation);
                        JsonContext jsonresult= Ebean.getServer(semester).createJsonContext();
                        String jsonoutput=jsonresult.toJsonString(allmessages,true);
                        ObjectNode result=Json.newObject();
                        ObjectNode notification=Json.newObject();
                        result.put("event","newmessage");
                        result.put("data",jsonoutput);
                        out.write(result.toString());
                        notification.put("event","noti");
                        notification.put("data",semesteruser.lastname+","+semesteruser.firstname+";"+jsonoutput);
                        if(connections.get(other)!=null){
                        connections.get(other).write(notification.toString());}
                        //Logger.warn("after adding message "+jsonoutput);
                        //return ok(jsonoutput);
                    }
                }

            }
        });

    }
   public static void notify(String user1,String user2,String message){
       connections.get(user1).write(message);
       connections.get(user2).write(message);
   }


}
