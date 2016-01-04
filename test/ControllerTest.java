import controllers.Application;
import controllers.account.settings.ModifySSH;
import controllers.account.settings.routes;
import models.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import play.mvc.Results;
import play.test.FakeApplication;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.TestServer;
import utils.AppException;
import utils.Hash;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;
import static utils.CreateDB.createServer;

/**
 * Created by Hao on 2016/1/4.
 */
public class ControllerTest {
    TestServer apps= testServer(9000);

    @Before
    public void cleanenv(){
        Path p= Paths.get(System.getProperty("user.home"), "data_dynamic");
        Path q=Paths.get(System.getProperty("user.home"),"HMS_Config");
        File f = p.toFile();
        File g=q.toFile();
        if(f.exists()){
            FileUtils.deleteQuietly(f);
        }
        if(g.exists()){
            FileUtils.deleteQuietly(g);
        }

        Helpers.start(apps);
        List<Class> entity = new ArrayList<Class>();
        entity.add(Semesteruser.class);
        entity.add(Assignment.class);
        entity.add(Exercise.class);
        entity.add(Lecture.class);
        entity.add(Message.class);
        entity.add(Repo.class);
        entity.add(Evaluation.class);
        entity.add(Handin.class);
        entity.add(ForumPost.class);
        entity.add(ForumThread.class);
        entity.add(Conversation.class);
        createServer("WS2016", entity);
    }

    @After
    public void closeserver(){
        Helpers.stop(apps);
    }
    public Semesteruser newSemesteruser(){
        Semesteruser semesteruser=new Semesteruser();
        semesteruser.id="7788414";
        semesteruser.save("WS2016");
        return semesteruser;
    }
    public Semesteruser newSemesteruser(String id){
        Semesteruser semesteruser=new Semesteruser();
        semesteruser.id=id;
        semesteruser.save("WS2016");
        return semesteruser;
    }

    public Lecture newLecture(){
        Lecture lecture = new Lecture();
        lecture.courseName="test";
        lecture.save("WS2016");
        return lecture;
    }
    @Test
    public void testDeleteSSH() {
        User owner= new User();
        owner.id="7788414";
        owner.email="a@a.com";
        owner.save("global");
        SSH ssh= new SSH();
        ssh.ssh="abc";
        ssh.title="title";
        ssh.sshowner=owner;
        ssh.save("global");
        FakeRequest request=new FakeRequest("POST","/settings/ssh_delete?sshid=1");
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
    }

    @Test
    public void testaddSemesterusertoLecture(){
        User owner= new User();
        owner.id="7788414";
        owner.email="a@a.com";
        owner.roles=UserRoll.Teachers.toString();
        owner.firstname="Hao";
        owner.lastname="Gao";
        owner.save("global");
        Lecture lecture=new Lecture();
        lecture.courseName="TestCourse";
        lecture.attendent=new ArrayList<>();
        lecture.save("WS2016");
        FakeRequest request=new FakeRequest("GET","/admin/user/WS2016/TestCourse/add").withSession("email","a@a.com");
        Result result=route(request);
        Semesteruser semesteruser=Semesteruser.findByEmail("a@a.com","WS2016");
        assertThat(semesteruser.lectures).hasSize(1);
    }

    @Test
    public void testdeleteSemesteruserfromlectureadmin(){
        User owner= new User();
        owner.id="7788414";
        owner.email="a@a.com";
        owner.roles=UserRoll.Teachers.toString();
        owner.firstname="Hao";
        owner.lastname="Gao";
        owner.save("global");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser("WS2016",owner);
        Lecture lecture=new Lecture();
        lecture.courseName="TestCourse";
        lecture.attendent.add(semesteruser);
        lecture.save("WS2016");

        FakeRequest request=new FakeRequest("GET","/admin/user/WS2016/TestCourse/delete").withSession("email","a@a.com");
        Result result=route(request);
        Lecture after=Lecture.getlecturebyname("TestCourse","WS2016");
        assertThat(after.attendent).hasSize(0);
    }

    @Test
    public void testdeleteAssignment(){
        User owner= new User();
        owner.id="7788414";
        owner.email="a@a.com";
        owner.roles=UserRoll.Teachers.toString();
        owner.firstname="Hao";
        owner.lastname="Gao";
        owner.save("global");
        Lecture lecture=new Lecture();
        lecture.courseName="TestCourse";
        lecture.save("WS2016");
        Assignment assignment=new Assignment();
        assignment.title="a";
        assignment.numberofexercise=4;
        assignment.totalpoints=80;
        assignment.uploadfile="abc";
        assignment.filename="1";
        assignment.addtionalinfo="test";
        assignment.deadline=new Date();
        assignment.semester="WS2016";
        assignment.isoptional=false;
        assignment.lecture=lecture;
        Handin handin = new Handin();
        handin.student=newSemesteruser();
        handin.marker=newSemesteruser("7788");
        handin.save("WS2016");
        assignment.handins.add(handin);
        assignment.isExpired();
        assignment.save("WS2016");
        FakeRequest request=new FakeRequest("GET","/admin/WS2016/TestCourse/a/delete_assignment" ).withSession("email","a@a.com");
        Result result=route(request);
        Assignment after=Assignment.findById("WS2016","1");
        assertThat(after).isNull();
    }


}
