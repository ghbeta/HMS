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
    static TestServer model= testServer(9000);

    @Before
    public static void cleanenv(){
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
    public static void closeserver(){
        Helpers.stop(model);
    }

   @Test
   public void a_testUser() throws AppException {
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
}