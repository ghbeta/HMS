import models.Token;
import models.User;
import models.UserRoll;
import org.apache.commons.io.FileUtils;
import org.fluentlenium.adapter.FluentTest;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.test.Helpers;
import play.test.TestServer;
import play.test.WithServer;
import utils.AppException;
import utils.Hash;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.with;
import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by Hao on 2015/12/29.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountTest extends FluentTest{
    public static TestServer app=testServer(9000);

    @BeforeClass
    public static void cleanenv() throws IOException {
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
        List<String> lines= Arrays.asList("Add assignment test", "fake content");
        Path p1= Paths.get(System.getProperty("user.home"),"Assignment1.txt");
        Files.write(p1, lines, Charset.forName("UTF-8"));

        Helpers.start(app);
    }

    @AfterClass
    public static void deleteTestFile() throws IOException {
        Path p= Paths.get(System.getProperty("user.home"), "Assignment1.txt");
        FileUtils.forceDelete(p.toFile());
        Helpers.stop(app);
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
    public void a0_testAbout(){
        goTo("http://192.168.0.198:9000");
        click("#about_page");
        await().atMost(5,TimeUnit.SECONDS).until("#used_works").areDisplayed();
    }
    @Test
    public void a_testRegistration(){
       goTo("http://192.168.0.198:9000");
       fill("#SignUpEmail").with("123@123.com");
        fill("#SignUpLastname").with("123");
        fill("#SignUpFirstname").with("123");
        fill("#SignUpPassword").with("123");
        click("#SignUpSubmit");
       //submit("#sb_form_go");
        await().atMost(5, TimeUnit.SECONDS).until(".label-success").areDisplayed();
       assertThat(find(".label-success").getText()).isEqualTo("You will receive a confirmation email soon. Check your email to activate your account.");
    }


    @Test
    public void b_testConfirmation(){
        User teacher = User.findByEmail("123@123.com","global");
        assertThat(teacher.confirmationToken).isNotEmpty();
        teacher.roles= UserRoll.Teachers.toString();
        teacher.update("global");
        String url="http://192.168.0.198:9000"+"/confirm/"+teacher.confirmationToken;
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
        goTo("http://192.168.0.198:9000");
        fill("#LoginEmail").with("123@123.com");
        fill("#LoginPassword").with("123");
        click("#LoginButton");
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");
    }


    @Test
    public void c_testSSH(){
        testSignin();
        String ssh="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDo1t61PiEAjqqfp9vKd9AaDmrxbMPiLfe9dC/F3Dm5QtftjpFasj1IrFvlQE9UXlJafOIKCcKb8yGeXDa1oXBD79n/mixFwec6LTRW34A3eBHxXl4Kr05FRkZyFvuItpAMPDG+N3xR3kZDRUZvQo7gSFWjq4/qv9d1+BKcOLOFo12GDh1QZ2T7lPE7+CqonTqQnpjAKpCQaghTjH4xwSpFdgFiT7V35K4Uiks/pA4lTC8Vv+5W4PUM86cjCb3Pn2CVGi7ExihLAKw+Xc9ju8wlFdwSqiNDP3Sxnt7u4Ch3U44+VxZpWaJ3lSFxhTPKm8lFxZyyTi2kboagGZ1CBQXX Hao@HAO-PC";
        //goTo("http://192.168.0.198:9000/");
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
        String url="http://192.168.0.198:9000"+"/email/"+token.token;
        goTo(url);
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find(".label-success").getText()).isEqualTo("Email successfully validated (456@456.com)");
        click("#UpdatePassword");
        click("#UpdatePasswordButton");
        await().atMost(5, TimeUnit.SECONDS);
        Token passwordtoken=Token.findTokenByUserId(teacher.id,"global");
        String passwordreseturl="http://192.168.0.198:9000"+"/reset/"+passwordtoken.token;
        goTo(passwordreseturl);
        await().atMost(5, TimeUnit.SECONDS);
        fill("#password1").with("456");
        fill("#password2").with("456");
        await().atMost(5, TimeUnit.SECONDS);
        click("#passwordSubmit");
        assertThat(find(".label-success").getText()).isEqualTo("Your password has been reset.");
    }

    @Test
    public void e_testHomepageResetPassword(){
        goTo("http://192.168.0.198:9000");
        click("#reset_home_password");
        await().until("#input_email_reset").areDisplayed();
        fill("#input_email_reset").with("456@456.com");
        click("#reset_password_button");
        await().untilPage().isLoaded();
        User teacher = User.findByEmail("456@456.com","global");
        Token passwordtoken=Token.findTokenByUserId(teacher.id,"global");
        String passwordreseturl="http://192.168.0.198:9000"+"/reset/"+passwordtoken.token;
        goTo(passwordreseturl);
        await().atMost(5, TimeUnit.SECONDS);
        fill("#password1").with("456");
        fill("#password2").with("456");
        await().atMost(5, TimeUnit.SECONDS);
        click("#passwordSubmit");
        assertThat(find(".label-success").getText()).isEqualTo("Your password has been reset.");
    }


    public void testNewSignin(){
        goTo("http://192.168.0.198:9000");
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

    //-----------------start course setting---------------------------------------------
    public void Signin(){
        goTo("http://192.168.0.198:9000");
        fill("#LoginEmail").with("456@456.com");
        fill("#LoginPassword").with("456");
        click("#LoginButton");
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");
    }

    public void StudentSignin(){
        goTo("http://192.168.0.198:9000");
        fill("#LoginEmail").with("a@a.com");
        fill("#LoginPassword").with("123");
        click("#LoginButton");
    }

    public void AssistantSignin(){
        goTo("http://192.168.0.198:9000");
        fill("#LoginEmail").with("b@b.com");
        fill("#LoginPassword").with("123");
        click("#LoginButton");
    }
    @Test
    public void g_createLocalCourse(){
        Signin();
        click("#CreateNewLecture");
        await().atMost(10, TimeUnit.SECONDS);
        click("option", withText("WS"));
        fill("#CourseYear").with("2016");
        fill("#CourseName").with("LocalLectureTest");
        click("#CourseModes");
        await().atMost(5, TimeUnit.SECONDS);
        fill("#total_assignment").with("12");
        fill("#optional_assignment").with("2");
        fill("#percentage_validassignment").with("0.5");
        fill("#percentage_exam").with("0.5");
        fill("#number_validassignment").with("10");
        fill("#createlecture_closingdate").with("03/10/2016");
        fill("#course_description").with("test course under local mode");
        click("#create_course_button");
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find(".label-success").getText()).isEqualTo("WS2016");

    }
    @Test
    public void h_createRemoteCourse(){
        Signin();
        click("#CreateNewLecture");
        await().atMost(10, TimeUnit.SECONDS);
        click("option", withText("WS"));
        fill("#CourseYear").with("2016");
        fill("#CourseName").with("RemoteLectureTest");
        fill("#createlecture_closingdate").with("03/10/2016");
        fill("#course_description").with("test course under remote mode");
        click("#create_course_button");
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find(".label-success").getText()).isEqualTo("WS2016");
    }

    @Test
    public void i_createAssignment(){
        String url="http://192.168.0.198:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        click("#create_local_homework");
        await().atMost(30, TimeUnit.SECONDS).until("#assignmentModal").areDisplayed();
        //fill("#number_exercise").with("4");
        fill("#total_points").with("80");
        fill("#assignment_deadline").with("03/10/2016");
        fill("#upload_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#additional_info").with("test adding new assignment");
        click("#submit_assignment");
        await().atMost(15, TimeUnit.SECONDS).until("#table_localinfo").isPresent();
        assertThat(find("#table_localinfo").getText()).isEqualTo("test adding new assignment");
    }

    @Test
    public void j_createAssignmentRemote(){
        String url="http://192.168.0.198:9000/admin/123/WS2016/RemoteLectureTest";
        Signin();
        goTo(url);
        click("#create_remote_homework");
        await().atMost(30, TimeUnit.SECONDS).until("#assignmentModal").areDisplayed();
        //fill("#number_exercise").with("1");
        fill("#total_points").with("20");
        fill("#assignment_deadline").with("03/10/2016");
        fill("#upload_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#additional_info").with("test adding new assignment");
        click("#submit_assignment");
        await().atMost(15, TimeUnit.SECONDS).until("#table_remoteinfo").isPresent();
        assertThat(find("#table_remoteinfo").getText()).isEqualTo("test adding new assignment");
    }
    @Test
    public void k_modifyAssignment(){
        String url="http://192.168.0.198:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        await().atMost(30,TimeUnit.SECONDS).until("#modify_assignment").isPresent();
        click("#modify_assignment");
        await().atMost(30,TimeUnit.SECONDS).until("#modify1").areDisplayed();
        fill("#modify_assignment_deadline").with("03/09/2016");
        fill("#modify_assignment_info").with("test modify assignment");
        click("#submit_modify_assignment");
        await().atMost(30,TimeUnit.SECONDS).until("#table_localinfo").isPresent();
        assertThat(find("#table_localinfo").getText()).isEqualTo("test modify assignment");
    }

    @Test
    public void l_modifyTerms(){
        String url="http://192.168.0.198:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        await().atMost(30,TimeUnit.SECONDS).until("#modify_terms").isPresent();
        click("#modify_terms");
        await().atMost(30,TimeUnit.SECONDS).until("#termsModal").areDisplayed();
        fill("#modify_numbervalid").with("9");
        click("#modify_terms_button");
    }

    @Test
    public void m_modifyDescription(){
        String url="http://192.168.0.198:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        click("#modify_description");
        await().atMost(10,TimeUnit.SECONDS).until("#descriptionModal").areDisplayed();
        fill("#add_description").with("a new test description");
        click("#submit_new_description");
        await().atMost(10,TimeUnit.SECONDS).until("#lecture_description").isPresent();
        assertThat(find("#lecture_description").getText()).isEqualTo("a new test description");
    }

    @Test
    public void n_testForum(){
        String url="http://192.168.0.198:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        click("#tab_forum");
        await().atMost(5,TimeUnit.SECONDS).until("#create_thread").isPresent();
        click("#create_thread");
        await().atMost(5,TimeUnit.SECONDS).until("#thread_title").isPresent();
        fill("#thread_title").with("test thread");
        fill("#thread_content").with("test thread content");
        click("#thread_submit");
        await().atMost(10,TimeUnit.SECONDS).until("#show_thread_title").areDisplayed();
        click("#show_thread_title");
        await().atMost(5,TimeUnit.SECONDS).until("#inside_thread").isPresent();
        assertThat(find("#inside_thread").getText()).isEqualTo("test thread");
        fill("#inside_reply").with("test reply");
        click("#submit_inside_reply");
        await().atMost(5,TimeUnit.SECONDS).until("#inside_success_reply").isPresent();
        assertThat(find("#inside_success_reply").getText()).isEqualTo("test reply");
    }

    @Test
    public void o_testAddStudentLocal(){
        String ssh="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCn1XqMk4EPUXUtBllEVMvr+d1B3rH6UCqtyQMKrJbC/9qkfpNVHcMvklmrXXRt9x7PO8xUzXto4qTZGNw0Q9sxaqXyUCRKmYrYqCrDuq0gQ6TCDSZQQJOzW3wDMUqdDkeQ2Zd94SQ3VWWmeGZFjWLq+Y5Cdb1b4F9z9m+uXVax91+/9ZxKbfHAP4fZQXevgvCiH//UPTYIXM3CBYgHbTmvXXhRluWFqayrTegysPft/mfEbuEpzg8qbkI5d1v7gxovLiqiLAUCxePnlUsG80cocFpWtxcDRY2Z1zXSwnyBaj11N0/nsGRGOeqI57gUFfbvEaUZwDbCUeg5wrEnz6Ml Win7Client@Win7Client-PC";
        goTo("http://192.168.0.198:9000/");
        StudentSignin();
        await().atMost(5, TimeUnit.SECONDS);
        click("#UserSetting");
        fill("#SSHTitle").with("studentSSH");
        fill("#SSHValue").with(ssh);
        click("#SSHButton");
        await().atMost(5, TimeUnit.SECONDS);
        click("#lecture_all");
        await().atMost(5,TimeUnit.SECONDS).until("#semester_tab").isPresent();
        click("#semester_tab");

        await().atMost(5,TimeUnit.SECONDS).until("#student_lectureLocalLectureTest").areDisplayed();
        await().untilPage().isLoaded();
        click("#student_lectureLocalLectureTest");
        await().atMost(5,TimeUnit.SECONDS).until("#locallecture_addstudent").isPresent();
        click("#locallecture_addstudent");
        await().atMost(10,TimeUnit.SECONDS).until("#student_homework_table").areDisplayed();
        assertThat(findFirst("#student_homework_table").isDisplayed());

    }

    @Test
    public void p_testAddStudentRemote(){
        StudentSignin();
        click("#lecture_all");
        await().untilPage().isLoaded();
        await().atMost(5,TimeUnit.SECONDS).until("#semester_tab").isPresent();
        click("#semester_tab");
        await().untilPage().isLoaded();
        await().atMost(5,TimeUnit.SECONDS).until("#student_lectureRemoteLectureTest").areDisplayed();
        await().untilPage().isLoaded();
        click("#student_lectureRemoteLectureTest");
        await().atMost(5,TimeUnit.SECONDS).until("#remotelecture_addstudent").isPresent();
        click("#remotelecture_addstudent");
        await().atMost(10,TimeUnit.SECONDS).until("#remote_student_homework_table").areDisplayed();
        assertThat(findFirst("#remote_student_homework_table").isDisplayed());
    }
    @Test
    public void q_testStudentHandin(){
        StudentSignin();
        click("#lecture_my");
        await().atMost(5,TimeUnit.SECONDS).until("#my_semester_tab").isPresent();
        click("#my_semester_tab");

        await().atMost(10,TimeUnit.SECONDS).until("#my_lecture_contentLocalLectureTest").areDisplayed();
        await().untilPage().isLoaded();
        click("#my_lecture_contentLocalLectureTest");
        await().atMost(15,TimeUnit.SECONDS).until("#handin_homework").areDisplayed();
        click("#handin_homework");
        await().atMost(5,TimeUnit.SECONDS).until("#handin1").areDisplayed();
        fill("#homework_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#homework_commit").with("test upload");
        click("#submit_homework");
        assertThat(find("#handin_status").getText()).isEqualTo("Handin exist");
    }

//    @Test
//    public void r_testStudentHandinRemote(){
//        StudentSignin();
//        click("#lecture_my");
//        await().atMost(5,TimeUnit.SECONDS).until("#my_semester_tab").isPresent();
//        click("#my_semester_tab");
//
//        await().atMost(10,TimeUnit.SECONDS).until("#my_lecture_contentRemoteLectureTest").areDisplayed();
//        await().untilPage().isLoaded();
//        click("#my_lecture_contentRemoteLectureTest");
//        await().atMost(15,TimeUnit.SECONDS).until("#handin_homework_remote").areDisplayed();
//        click("#handin_homework_remote");
//        assertThat(find("#remote_handin_result").getText()).isEqualTo("0.0/20.0");
//    }
    @Test
    public void s_testStudentRevert(){
        String url="http://192.168.0.198:9000/students/Gao/WS2016/LocalLectureTest";
        StudentSignin();
        goTo(url);
        click("#revert_handin");
        await().atMost(5,TimeUnit.SECONDS).until("#handin_status_not").areDisplayed();
        assertThat(find("#handin_status_not").getText()).isEqualTo("No Handin");
    }
//    @Test
//    public void t_testStudentRemoteRevert(){
//        String url="http://192.168.0.198:9000/students/Gao/WS2016/RemoteLectureTest";
//        StudentSignin();
//        goTo(url);
//        click("#revert_remote_handin");
//        assertThat(find("#remote_handin_result").getText()).isEqualTo("0/20.0");
//    }
    @Test
    public void u_testEvaluation(){
        String url="http://192.168.0.198:9000/students/Gao/WS2016/LocalLectureTest";
        StudentSignin();
        goTo(url);
        await().atMost(15,TimeUnit.SECONDS).until("#handin_homework").areDisplayed();
        click("#handin_homework");
        await().atMost(5,TimeUnit.SECONDS).until("#handin1").areDisplayed();
        fill("#homework_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#homework_commit").with("test upload");
        click("#submit_homework");

        click("#loggeddropdown");
        await().atMost(5,TimeUnit.SECONDS).until("#loggeduser").areDisplayed();
        click("#loggeduser");

        String url1="http://192.168.0.198:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url1);
        click("#tab_correction");
        await().atMost(5,TimeUnit.SECONDS).until("#open_assignment").isPresent();
        click("#open_assignment");
        await().atMost(5,TimeUnit.SECONDS).until("#add_eval_button").areDisplayed();
        click("#add_eval_button");
        await().atMost(5,TimeUnit.SECONDS).until("#eval17788414").areDisplayed();
        fill("#earndpoints_1").with("70");
        fill("#comments_1").with("test comments");
        click("#submit_eval");

//        await().atMost(5,TimeUnit.SECONDS).until("#open_assignment").areDisplayed();
        await().untilPage().isLoaded();
        //goTo(url1);
        // await().untilPage().isLoaded();
        // click("#tab_correction");
        //await().atMost(5,TimeUnit.SECONDS).until("#open_assignment").isPresent();
        //click("#open_assignment");
        //await().atMost(10,TimeUnit.SECONDS).until("#eval_result").areDisplayed();
        //assertThat(find("#eval_result").getText()).isEqualTo("40.0/80.0");


    }
    @Test
    public void v_testDeleteStudent(){
        String url="http://192.168.0.198:9000/students/Gao/WS2016/RemoteLectureTest";
        StudentSignin();
        goTo(url);
        await().atMost(5,TimeUnit.SECONDS).until("#remotelecture_deletestudent").isPresent();
        click("#remotelecture_deletestudent");
        await().atMost(5,TimeUnit.SECONDS).until("#remotelecture_addstudent").areDisplayed();
        assertThat(findFirst("#remotelecture_addstudent").isDisplayed());
    }

    @Test
    public void w_testAddRepoafter(){
        StudentSignin();
        click("#UserSetting");
        await().atMost(5,TimeUnit.SECONDS).until("#ssh_delete").areDisplayed();
        click("#ssh_delete");
        String url="http://192.168.0.198:9000/students/Gao/WS2016/RemoteLectureTest";
        goTo(url);
        await().atMost(5,TimeUnit.SECONDS).until("#remotelecture_addstudent").areDisplayed();
        click("#remotelecture_addstudent");
        await().atMost(10,TimeUnit.SECONDS).until("#remoterepo_generate").areDisplayed();
        await().untilPage().isLoaded();
        //-----------add ssh again
        String ssh="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCn1XqMk4EPUXUtBllEVMvr+d1B3rH6UCqtyQMKrJbC/9qkfpNVHcMvklmrXXRt9x7PO8xUzXto4qTZGNw0Q9sxaqXyUCRKmYrYqCrDuq0gQ6TCDSZQQJOzW3wDMUqdDkeQ2Zd94SQ3VWWmeGZFjWLq+Y5Cdb1b4F9z9m+uXVax91+/9ZxKbfHAP4fZQXevgvCiH//UPTYIXM3CBYgHbTmvXXhRluWFqayrTegysPft/mfEbuEpzg8qbkI5d1v7gxovLiqiLAUCxePnlUsG80cocFpWtxcDRY2Z1zXSwnyBaj11N0/nsGRGOeqI57gUFfbvEaUZwDbCUeg5wrEnz6Ml Win7Client@Win7Client-PC";
        click("#UserSetting");
        fill("#SSHTitle").with("studentSSH");
        fill("#SSHValue").with(ssh);
        click("#SSHButton");
        await().untilPage().isLoaded();
        //-------to homepage again
        goTo(url);
        await().atMost(10,TimeUnit.SECONDS).until("#remoterepo_generate").areDisplayed();
        click("#remoterepo_generate");
        await().atMost(10,TimeUnit.SECONDS).until("#remoterepo_exist").areDisplayed();
        assertThat(find("#remoterepo_exist").getText()).isEqualTo("Last Update of Repository:");
    }

    @Test
    public void x_testMessage(){
        String url="http://192.168.0.198:9000/students/Gao/WS2016/RemoteLectureTest";
        StudentSignin();
        goTo(url);
        click("#open_conversation");
        await().until("#messageb2d7d2d13aed54c2ed7feb538b382b42").areDisplayed();
        fill("#message_content").with("test message");
        click("#send_message_button");
        await().untilPage().isLoaded();
        click("#open_conversation");
        await().until("#messageb2d7d2d13aed54c2ed7feb538b382b42").areDisplayed();
        fill("#message_content").with("test message again");
        click("#send_message_button");
        await().untilPage().isLoaded();
        click("#my_messages");
        await().untilPage().isLoaded();
        click("#select_semester");
        await().until("#target_semester").areDisplayed();
        click("#target_semester");
        await().until("#chat_user").areDisplayed();
        await().untilPage().isLoaded();
        click("#chat_user");
        await().untilPage().isLoaded();
        fill("#message_reply_content").with("test reply");
        click("#message_reply_button");
        await().untilPage().isLoaded();
        assertThat(findFirst("small",withText("test reply")));
    }

}
