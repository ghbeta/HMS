package controllers.lectures;

import Permission.Securedstudents;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ForumThread;
import models.Lecture;
import models.Semesteruser;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Hao on 2015/11/10.
 */
public class Lectureforum extends Controller{

    @Security.Authenticated(Securedstudents.class)
    @BodyParser.Of(BodyParser.Json.class)
    public static Result createthread(String semester,String lecturename){
        JsonNode json = request().body().asJson();
        Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        try {
            ForumThread forumThread = new ForumThread();
            Iterator<JsonNode> iter = json.elements();
            int i = 0;
            while (iter.hasNext()) {
                if (i == 0) {
                    forumThread.title = iter.next().findPath("value").textValue();
                }
                if (i == 1) {
                    forumThread.content = iter.next().findPath("value").textValue();
                }
                i++;
            }
            forumThread.creator = currentuser;
            forumThread.lecture = currentlecture;
            forumThread.creattime = new Date();
            forumThread.save(semester);
            //ObjectMapper mapper=new ObjectMapper();
            //return ok(mapper.writeValueAsString(currentlecture.threads));
            //List<ForumThread> threads=currentlecture.threads;
            JsonContext jsonContext = Ebean.getServer(semester).createJsonContext();
            List<ForumThread> results=ForumThread.findByLecture(semester,currentlecture);
            String resultjson=jsonContext.toJsonString(results);
            Logger.warn("json result "+resultjson);
            return ok(resultjson);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return badRequest();
        }

    }
}
