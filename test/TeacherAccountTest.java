import models.Token;
import models.User;
import models.UserRoll;
import org.apache.commons.io.FileUtils;
import org.fluentlenium.adapter.FluentTest;

import org.junit.*;
import org.junit.runners.MethodSorters;
import play.test.Helpers;
import play.test.WithServer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.with;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by Hao on 2015/12/29.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TeacherAccountTest extends FluentTest{
    @BeforeClass
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

        Helpers.start(testServer(9000));
    }

//    @Before
//    public void startserver(){
//        Helpers.start(testServer(9000));
//    }
//
//    @After
//    public void closeserver(){
//        Helpers.stop(testServer(9000));
//    }
    @Test
    public void first_testRegistration(){
       goTo("http://localhost:9000");
       fill("#SignUpEmail").with("123@123.com");
        fill("#SignUpLastname").with("123");
        fill("#SignUpFirstname").with("123");
        fill("#SignUpPassword").with("123");
        click("#SignUpSubmit");
       //submit("#sb_form_go");
        await().atMost(5, TimeUnit.SECONDS);
       assertThat(find(".label-success").getText()).isEqualTo("You will receive a confirmation email soon. Check your email to activate your account.");
    }


    @Test
    public void second_testConfirmation(){
        User teacher = User.findByEmail("123@123.com","global");
        assertThat(teacher.confirmationToken).isNotEmpty();
        teacher.roles= UserRoll.Teachers.toString();
        teacher.update("global");
        String url="http://localhost:9000"+"/confirm/"+teacher.confirmationToken;
        goTo(url);
        await().atMost(5, TimeUnit.SECONDS);
        click("#DirectLogin");
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");

    }


}
