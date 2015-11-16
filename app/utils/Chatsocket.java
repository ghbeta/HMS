package utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Conversation;
import models.Semesteruser;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Hao on 2015/11/16.
 */
public class Chatsocket {

    public static HashMap<String,WebSocket.Out<String>> connections = new HashMap<String, WebSocket.Out<String>>();
    public static void start(WebSocket.In<String> in, WebSocket.Out<String> out){
        //connections.put(userid1,out);
        //connections.put(userid2,out);
        in.onMessage(new F.Callback<String>() {
            @Override
            public void invoke(String event) throws Throwable {
                JsonNode inmsg = Json.parse(event);
                Logger.warn(event);
                if(inmsg.findPath("event").asText().equals("allconversations")){

                    JsonNode requestbody=inmsg.findPath("data");
                    Logger.warn(requestbody.findPath("semester").asText());
                    String semester=requestbody.findPath("semester").asText();
                    Logger.warn(requestbody.findPath("email").asText());
                    String email=requestbody.findPath("email").asText();
                    Semesteruser currentuser=Semesteruser.findByEmail(email,semester);
                    if(currentuser!=null){
                        List<Conversation> conversations=Conversation.getConversationByOneuser(semester,currentuser);
                        JsonContext json= Ebean.getServer(semester).createJsonContext();
                        String jsonoutput=json.toJsonString(conversations);
                        ObjectNode result = Json.newObject();
                        result.put("event","allconversations");
                        result.put("data",jsonoutput);
                        Logger.warn(result.toString());
                        //return ok(jsonoutput);
                        out.write(result.toString());
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
