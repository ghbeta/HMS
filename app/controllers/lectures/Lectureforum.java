package controllers.lectures;

import Permission.Securedstudents;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.lectures.user.*;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
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
    public static Result forumredirect(String semester,String lecturename){
        Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        //User user1=User.findByEmail(ctx().session().get("email"),"global");
        if(currentuser.roles.equals(UserRoll.Assistants.toString())||currentuser.roles.equals(UserRoll.Teachers.toString())){
            //return ok(views.html.lectures.admin.lecturehome.render(user1, currentuser, currentlecture, null));
            return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname, semester, currentlecture.courseName));
        }
        else{
            return redirect(controllers.lectures.user.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
        }
    }

    @Security.Authenticated(Securedstudents.class)
    public static Result createthread(String user,String semester,String lecturename){
        JsonNode json = request().body().asJson();
        User user1=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        DynamicForm threadform= Form.form().bindFromRequest();
        try {
            ForumThread forumThread = new ForumThread();
//            Iterator<JsonNode> iter = json.elements();
//            int i = 0;
//            while (iter.hasNext()) {
//                if (i == 0) {
//                    forumThread.title = iter.next().findPath("value").textValue();
//                }
//                if (i == 1) {
//                    forumThread.content = iter.next().findPath("value").textValue();
//                }
//                i++;
//            }
            forumThread.title=threadform.get("title");
            forumThread.content=threadform.get("content");
            forumThread.creator = currentuser;
            forumThread.lecture = currentlecture;
            forumThread.creattime = new Date();
            forumThread.lastupdate=new Date();
            forumThread.setLastupdatetimestamp();
            forumThread.save(semester);
            //ObjectMapper mapper=new ObjectMapper();
            //return ok(mapper.writeValueAsString(currentlecture.threads));
            //List<ForumThread> threads=currentlecture.threads;
//            JsonContext jsonContext = Ebean.getServer(semester).createJsonContext();
//            List<ForumThread> results=ForumThread.findByLecture(semester,currentlecture);
//            String resultjson=jsonContext.toJsonString(results);
//            Logger.warn("json result "+resultjson);
            if(currentuser.roles.equals(UserRoll.Assistants.toString())||currentuser.roles.equals(UserRoll.Teachers.toString())){
                //return ok(views.html.lectures.admin.lecturehome.render(user1, currentuser, currentlecture, null));
             return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(currentuser.lastname, semester, currentlecture.courseName));
            }
            else{
                return redirect(controllers.lectures.user.routes.Lecturehome.generatelecturehome(currentuser.lastname,semester,currentlecture.courseName));
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return badRequest();
        }

    }

    @Security.Authenticated(Securedstudents.class)
    public static Result getthread(String semester,String lecturename,String threadid){
        User user1=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        ForumThread thread=ForumThread.findById(semester,threadid);
        List<ForumThread> allthreads=ForumThread.findByLecture(semester,currentlecture);
        if(currentuser.roles.equals(UserRoll.Assistants.toString())||currentuser.roles.equals(UserRoll.Teachers.toString())){
            return redirect(views.html.lectures.admin.lecturehome.render(user1, currentuser, currentlecture, thread,allthreads));
             //return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehomeforum(currentuser.lastname, semester, currentlecture.courseName,threadid));
        }else{
            return redirect(views.html.lectures.user.lecturehome.render(user1, currentuser, currentlecture, thread,allthreads));
        }
    }

    @Security.Authenticated(Securedstudents.class)
    public static Result getmythreads(String semester,String lecturename){
        User user1=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        List<ForumThread> mythreads=ForumThread.findByLectureByStudent(semester,currentlecture,currentuser);
        if(currentuser.roles.equals(UserRoll.Assistants.toString())||currentuser.roles.equals(UserRoll.Teachers.toString())){
            return redirect(views.html.lectures.admin.lecturehome.render(user1, currentuser, currentlecture,null,mythreads));
            //return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehomeforum(currentuser.lastname, semester, currentlecture.courseName,threadid));
        }else{
            return redirect(views.html.lectures.user.lecturehome.render(user1, currentuser, currentlecture, null,mythreads));
        }
    }


    @Security.Authenticated(Securedstudents.class)
    public static Result createpost(String semester,String lecturename,String threadid){
        User user1=User.findByEmail(ctx().session().get("email"),"global");
        Semesteruser currentuser=Semesteruser.findByEmail(ctx().session().get("email"),semester);
        Lecture currentlecture=Lecture.getlecturebyname(lecturename,semester);
        ForumThread thread=ForumThread.findById(semester,threadid);
        try{
        DynamicForm postform= Form.form().bindFromRequest();
            ForumPost post=new ForumPost();
            post.content=postform.get("postcontent");
            post.creator=currentuser;
            post.creattime=new Date();
            post.lecture=currentlecture;
            post.parent=thread;
            thread.lastupdate=new Date();
            thread.setLastupdatetimestamp();
            post.setTimestamp();
            post.save(semester);
            thread.update(semester);
        return redirect(routes.Lectureforum.getthread(semester,lecturename,threadid));}
        catch(Exception e){
            Logger.warn(e.getMessage());
            return redirect(routes.Lectureforum.getthread(semester, lecturename, threadid));
        }
    }
}
