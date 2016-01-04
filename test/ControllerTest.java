import controllers.Application;
import controllers.account.settings.ModifySSH;
import controllers.account.settings.routes;
import models.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.TestServer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Http.Status.OK;
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

}
