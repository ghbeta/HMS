import org.apache.commons.io.FileUtils;
import org.fluentlenium.adapter.FluentTest;

import org.junit.Before;
import org.junit.Test;
import play.test.Helpers;

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
public class TeacherAccountTest extends FluentTest {
    @Before
    public void startserver(){
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

    @Test
    public void testRegistration(){
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
}
