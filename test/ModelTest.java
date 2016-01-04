import models.*;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;
import play.test.Helpers;
import play.test.TestServer;
import utils.AppException;
import utils.Hash;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.test.Helpers.testServer;
import static utils.CreateDB.createServer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModelTest {
    TestServer model= testServer(9000);

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

        Helpers.start(model);
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
        Helpers.stop(model);
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

    public Message newMessage(){
        Message message=new Message();
        message.messagebody="abc";
        message.semester="WS2016";
        message.setTimestamp();
        message.save("WS2016");
        return message;
    }

    public Handin newHandin(){
        Handin handin=new Handin();
        handin.totalpoints=100f;
        handin.save("WS2016");
        return handin;
    }

   @Test
   public void testUser() throws AppException {
       User student = new User();
       student.id="7788414";
       student.email="a@a.com";
       student.firstname="Hao";
       student.lastname="Gao";
       student.roles= UserRoll.Students.toString();
       student.setUserHash();
       student.passwordHash = Hash.createPassword("123");
       student.dateCreation=new Date();
       student.validated=true;
       student.save("global");
       assertThat(User.findById("7788414","global")).isNotNull();
   }
    @Test
    public void testSSH(){
        User owner= new User();
        owner.id="7788414";
        owner.save("global");
        SSH ssh= new SSH();
        ssh.ssh="abc";
        ssh.title="title";
        ssh.sshowner=owner;
        ssh.save("global");
        assertThat(SSH.findById("1")).isNotNull();
    }

    @Test
    public void testRepo(){
        Repo repo=new Repo();
        repo.setRepofilepath("abc");
        repo.repopath="abc2";
        repo.owner.add(newSemesteruser());

        repo.course=newLecture();
        repo.semester="WS2016";
        repo.save("WS2016");
        assertThat(Repo.findRepoByLectureAndOwner("WS2016",repo.owner.get(0),repo.course)).isNull();
    }

    @Test
    public void testMessage(){
        Message message=new Message();
        message.messagebody="abc";
        message.semester="WS2016";
        message.setTimestamp();
        Conversation conversation= new Conversation();
        conversation.semester="WS2016";
        conversation.save("WS2016");
        message.conversation=conversation;

        message.sender=newSemesteruser();
        message.save("WS2016");
        assertThat(Message.findAllByConversation("WS2016",conversation)).hasSize(1);
    }

    @Test
    public void testEvaluation(){
        Evaluation evaluation=new Evaluation();
       evaluation.lecture=newLecture();
        evaluation.student=newSemesteruser();
        evaluation.setPerformance("WS2016",evaluation.lecture,evaluation.student);
        evaluation.save("WS2016");
        assertThat(Evaluation.findByLectureAndUser("WS2016",evaluation.lecture,evaluation.student)).isNotNull();
    }

    @Test
    public void testConversation(){
        Conversation conversation=new Conversation();
        conversation.user1=newSemesteruser();
        conversation.user2=newSemesteruser("7788520");
        conversation.semester="WS2016";
        conversation.messages.add(newMessage());
        conversation.save("WS2016");
        assertThat(Conversation.getConversationById("WS2016","1")).isNotNull();

    }

    @Test
    public void testExercise(){
        Exercise exercise=new Exercise();
        exercise.title="title";
        exercise.semester="WS2016";
        exercise.totalpoints=100f;
        exercise.earndpoints=80f;
        exercise.comments="abc";
        exercise.handin=newHandin();
        exercise.save("WS2016");
        assertThat(exercise.id).isEqualTo("1");
    }

    @Test
    public void testForumPost(){
        ForumPost forumPost=new ForumPost();
        forumPost.title="a";
        forumPost.content="b";
        forumPost.creattime=new Date();
        forumPost.setTimestamp();
        ForumThread thread=new ForumThread();
        thread.title="b";
        thread.save("WS2016");
        forumPost.parent=thread;
        forumPost.creator=newSemesteruser();
        forumPost.lecture=newLecture();
        forumPost.save("WS2016");
        assertThat(forumPost.id).isEqualTo("1");
    }

    @Test
    public void testHandin(){
        Handin handin = new Handin();
        handin.student=newSemesteruser();
        handin.marker=newSemesteruser("7788");
        handin.lecture=newLecture();
        Assignment assignment=new Assignment();
        assignment.title="assignment1";
        assignment.save("WS2016");
        handin.assignment=assignment;
        Exercise exercise=new Exercise();
        exercise.title="title";
        exercise.save("WS2016");
        handin.exercises.add(exercise);
        handin.totalpoints=10f;
        handin.earndpoints=10f;
        handin.handin=new Date();
        handin.setishandin();
        handin.setIsvalid();
        handin.setTotalpoints();
        handin.setEarndpoints();
        handin.save("WS2016");
        assertThat(Handin.getOptionalAssignmentofStudentsinLecture("WS2016",handin.lecture,handin.student)).hasSize(0);
    }

    @Test
    public void testForumThread(){
        ForumThread forumThread=new ForumThread();
        forumThread.title="a";
        forumThread.content="b";
        forumThread.creattime=new Date();
        forumThread.lastupdate=new Date();
        forumThread.setLastupdatetimestamp();
        forumThread.lecture=newLecture();
        forumThread.creator=newSemesteruser();
        ForumPost forumPost=new ForumPost();
        forumPost.title="a";
        forumPost.content="b";
        forumPost.creattime=new Date();
        forumPost.setTimestamp();
        forumPost.save("WS2016");
        forumThread.replyposts.add(forumPost);
        forumThread.save("WS2016");
        assertThat(ForumThread.findByLectureByStudent("WS2016",forumThread.lecture,forumThread.creator)).isNotNull();
    }

    @Test
    public void testAssignment(){
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
        assignment.lecture=newLecture();
        Handin handin = new Handin();
        handin.student=newSemesteruser();
        handin.marker=newSemesteruser("7788");
        handin.save("WS2016");
        assignment.handins.add(handin);
        assignment.isExpired();
        assignment.save("WS2016");
        assertThat(assignment.id).isEqualTo("1");
    }

    @Test
    public void testSemesteruser(){
        Semesteruser semesteruser=new Semesteruser();
        semesteruser.semester="WS2016";
        semesteruser.lectures.add(newLecture());
        Handin handin=new Handin();
        handin.earndpoints=50f;
        handin.save("WS2016");
        semesteruser.handins.add(handin);
        Repo repo=new Repo();
        repo.repopath="a";
        repo.save("WS2016");
        semesteruser.repos.add(repo);
        semesteruser.correctedhandins.add(handin);
        Message message=new Message();
        message.date=new Date();
        message.save("WS2016");
        semesteruser.messages.add(message);
        Evaluation evaluation=new Evaluation();
        evaluation.performance=5f;
        evaluation.save("WS2016");
        ForumThread forumThread=new ForumThread();
        forumThread.title="test";
        forumThread.save("WS2016");
        semesteruser.threads.add(forumThread);
        ForumPost forumPost=new ForumPost();
        forumPost.title="aa";
        forumPost.save("WS2016");
        semesteruser.posts.add(forumPost);
        semesteruser.email="aa";
        semesteruser.id="77884145555";
        semesteruser.lastname="aa";
        semesteruser.firstname="bb";
        semesteruser.roles="sss";
        semesteruser.userHash="666";
        semesteruser.save("WS2016");
        assertThat(semesteruser.id).isEqualTo("77884145555");
    }
}