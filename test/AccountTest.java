import models.Token;
import models.User;
import models.UserRoll;
import org.apache.commons.io.FileUtils;
import org.fluentlenium.adapter.FluentTest;

import org.junit.*;
import org.junit.runners.MethodSorters;
import play.test.Helpers;
import play.test.WithServer;
import utils.AppException;
import utils.Hash;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.with;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by Hao on 2015/12/29.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountTest extends FluentTest{
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
    public void a_testRegistration(){
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
    public void b_testConfirmation(){
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
//        click("#loggeddropdown");
//        await().atMost(5, TimeUnit.SECONDS);
//        click("#loggeduser");
//        assertThat(find(".label-success").getText()).isEqualTo("You have been logged out");
    }

    public void testSignin(){
        goTo("http://localhost:9000");
        fill("#LoginEmail").with("123@123.com");
        fill("#LoginPassword").with("123");
        click("#LoginButton");
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");
    }


    @Test
    public void c_testSSH(){
        testSignin();
        String ssh="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDo1t61PiEAjqqfp9vKd9AaDmrxbMPiLfe9dC/F3Dm5QtftjpFasj1IrFvlQE9UXlJafOIKCcKb8yGeXDa1oXBD79n/mixFwec6LTRW34A3eBHxXl4Kr05FRkZyFvuItpAMPDG+N3xR3kZDRUZvQo7gSFWjq4/qv9d1+BKcOLOFo12GDh1QZ2T7lPE7+CqonTqQnpjAKpCQaghTjH4xwSpFdgFiT7V35K4Uiks/pA4lTC8Vv+5W4PUM86cjCb3Pn2CVGi7ExihLAKw+Xc9ju8wlFdwSqiNDP3Sxnt7u4Ch3U44+VxZpWaJ3lSFxhTPKm8lFxZyyTi2kboagGZ1CBQXX Hao@HAO-PC";
        //goTo("http://localhost:9000/");
        await().atMost(5, TimeUnit.SECONDS);
        click("#UserSetting");
        fill("#SSHTitle").with("teacherSSH");
        fill("#SSHValue").with(ssh);
        click("#SSHButton");
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find("#ssh_title").getText()).isEqualTo("teacherSSH");
        assertThat(find("#ssh_value").getText()).isEqualTo(ssh);

    }
    @Test
    public void d_testResetEmailAndPassword(){
        testSignin();
        click("#UserSetting");
        await().atMost(5, TimeUnit.SECONDS);
        click("#UpdateEmail");
        fill("#inputUpdateEmail").with("456@456.com");
        click("#UpdateEmailButton");
        await().atMost(5, TimeUnit.SECONDS);
        User teacher = User.findByEmail("123@123.com","global");
        Token token=Token.findTokenByUserId(teacher.id,"global");
        String url="http://localhost:9000"+"/email/"+token.token;
        goTo(url);
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find(".label-success").getText()).isEqualTo("Email successfully validated (456@456.com)");
        click("#UpdatePassword");
        click("#UpdatePasswordButton");
        await().atMost(5, TimeUnit.SECONDS);
        Token passwordtoken=Token.findTokenByUserId(teacher.id,"global");
        String passwordreseturl="http://localhost:9000"+"/reset/"+passwordtoken.token;
        goTo(passwordreseturl);
        await().atMost(5, TimeUnit.SECONDS);
        fill("#password1").with("456");
        fill("#password2").with("456");
        await().atMost(5, TimeUnit.SECONDS);
        click("#passwordSubmit");
        assertThat(find(".label-success").getText()).isEqualTo("Your password has been reset.");
    }

    @Test
    public void e_testNewSignin(){
        goTo("http://localhost:9000");
        fill("#LoginEmail").with("456@456.com");
        fill("#LoginPassword").with("456");
        click("#LoginButton");
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");
    }

    @Test
    public void f_testAddOtherUser() throws AppException {
        User student = new User();
        student.id="7788414";
        student.email="a@a.com";
        student.firstname="Hao";
        student.lastname="Gao";
        student.roles=UserRoll.Students.toString();
        student.setUserHash();
        student.passwordHash = Hash.createPassword("123");
        student.dateCreation=new Date();
        student.validated=true;
        student.save("global");

        User assistent = new User();
        assistent.id="externa-b-c";
        assistent.email="b@b.com";
        assistent.firstname="Wei";
        assistent.lastname="Deng";
        assistent.roles=UserRoll.Assistants.toString();
        assistent.setUserHash();
        assistent.passwordHash=Hash.createPassword("123");
        assistent.dateCreation=new Date();
        assistent.validated=true;
        assistent.save("global");
        List<User> tests=User.findAll("global");
        assertThat(tests).hasSize(4);
    }

}
