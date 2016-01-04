import models.User;
import org.apache.commons.io.FileUtils;
import org.fluentlenium.adapter.FluentTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.test.Helpers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static play.test.Helpers.testServer;

/**
 * Created by Hao on 2016/1/2.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CourseTest extends FluentTest {
    @BeforeClass
    public static void generateTestFile() throws IOException {
        List<String> lines= Arrays.asList("Add assignment test","fake content");
        Path p= Paths.get(System.getProperty("user.home"),"Assignment1.txt");
        Files.write(p,lines, Charset.forName("UTF-8"));
        //Helpers.start(testServer(9000));
    }
    @AfterClass
    public static void deleteTestFile() throws IOException {
        Path p= Paths.get(System.getProperty("user.home"), "Assignment1.txt");
        FileUtils.forceDelete(p.toFile());
        Helpers.stop(AccountTest.app);
    }

    public void Signin(){
        goTo("http://localhost:9000");
        fill("#LoginEmail").with("456@456.com");
        fill("#LoginPassword").with("456");
        click("#LoginButton");
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");
    }

    public void StudentSignin(){
        goTo("http://localhost:9000");
        fill("#LoginEmail").with("a@a.com");
        fill("#LoginPassword").with("123");
        click("#LoginButton");
    }

    public void AssistantSignin(){
        goTo("http://localhost:9000");
        fill("#LoginEmail").with("b@b.com");
        fill("#LoginPassword").with("123");
        click("#LoginButton");
    }
    @Test
    public void a0_createLocalCourse(){
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
    public void a_createRemoteCourse(){
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
    public void b0_createAssignment(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        click("#create_local_homework");
        await().atMost(30, TimeUnit.SECONDS).until("#assignmentModal").areDisplayed();
        fill("#number_exercise").with("4");
        fill("#total_points").with("80");
        fill("#assignment_deadline").with("03/10/2016");
        fill("#upload_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#additional_info").with("test adding new assignment");
        click("#submit_assignment");
        await().atMost(15, TimeUnit.SECONDS).until("#table_localinfo").isPresent();
        assertThat(find("#table_localinfo").getText()).isEqualTo("test adding new assignment");
    }

    @Test
    public void b_createAssignmentRemote(){
        String url="http://localhost:9000/admin/123/WS2016/RemoteLectureTest";
        Signin();
        goTo(url);
        click("#create_remote_homework");
        await().atMost(30, TimeUnit.SECONDS).until("#assignmentModal").areDisplayed();
        fill("#number_exercise").with("1");
        fill("#total_points").with("20");
        fill("#assignment_deadline").with("03/10/2016");
        fill("#upload_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#additional_info").with("test adding new assignment");
        click("#submit_assignment");
        await().atMost(15, TimeUnit.SECONDS).until("#table_remoteinfo").isPresent();
        assertThat(find("#table_remoteinfo").getText()).isEqualTo("test adding new assignment");
    }
    @Test
    public void c_modifyAssignment(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
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
    public void d_modifyTerms(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        await().atMost(30,TimeUnit.SECONDS).until("#modify_terms").isPresent();
        click("#modify_terms");
        await().atMost(30,TimeUnit.SECONDS).until("#termsModal").areDisplayed();
        fill("#modify_numbervalid").with("9");
        click("#modify_terms_button");
    }

    @Test
    public void f_modifyDescription(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
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
    public void g_testForum(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
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
    public void h0_testAddStudentLocal(){
        String ssh="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC8Cv4/YtLkjLZbIMtsRbp4sZOpG7aD4BHEvMLpsUMKfP+4MwIk9a0YBpHMfB+RHzDhN6UyG/ZKTmHbGnLTAQ2XxUXXfmSi8qHqOkTFsBokWz4MLWtoanIkZhoHM22csZVeESq7bYUVhqBrEVGUA5ys9xqG9om/Sm2w4zDGturHgMoZeRjO8lZ2WyAPTA+IJIpXJBJ+LwvY74RkW0CzP3Aoqszgu+XXtLjyRaJCuz3sSCoj6mqbxZAP2Vt7TXUoA3WFausd3Y6Lk8kJMZWR1M5N0hHRgu+OgJXlzV4ZlQVt6vj6mgMQ8gCpv/CAVw4PpMbomM1YjI1L8O9SurXUbrjp Administrator@china-9aa05637d";
        goTo("http://localhost:9000/");
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
    public void h_testAddStudentRemote(){
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
    public void i0_testStudentHandin(){
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

    @Test
    public void i_testStudentHandinRemote(){
        StudentSignin();
        click("#lecture_my");
        await().atMost(5,TimeUnit.SECONDS).until("#my_semester_tab").isPresent();
        click("#my_semester_tab");

        await().atMost(10,TimeUnit.SECONDS).until("#my_lecture_contentRemoteLectureTest").areDisplayed();
        await().untilPage().isLoaded();
        click("#my_lecture_contentRemoteLectureTest");
        await().atMost(15,TimeUnit.SECONDS).until("#handin_homework_remote").areDisplayed();
        click("#handin_homework_remote");
        assertThat(find("#remote_handin_result").getText()).isEqualTo("0.0/20.0");
    }
    @Test
    public void j0_testStudentRevert(){
        String url="http://localhost:9000/students/Gao/WS2016/LocalLectureTest";
        StudentSignin();
        goTo(url);
        click("#revert_handin");
        await().atMost(5,TimeUnit.SECONDS).until("#handin_status_not").areDisplayed();
        assertThat(find("#handin_status_not").getText()).isEqualTo("No Handin");
    }
    @Test
    public void j_testStudentRemoteRevert(){
        String url="http://localhost:9000/students/Gao/WS2016/RemoteLectureTest";
        StudentSignin();
        goTo(url);
        click("#revert_remote_handin");
        assertThat(find("#remote_handin_result").getText()).isEqualTo("0/20.0");
    }
    @Test
    public void k_testEvaluation(){
        String url="http://localhost:9000/students/Gao/WS2016/LocalLectureTest";
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

        String url1="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url1);
        click("#tab_correction");
        await().atMost(5,TimeUnit.SECONDS).until("#open_assignment").isPresent();
        click("#open_assignment");
        await().atMost(5,TimeUnit.SECONDS).until("#add_eval_button").areDisplayed();
        click("#add_eval_button");
        await().atMost(5,TimeUnit.SECONDS).until("#eval17788414").areDisplayed();
        fill("#earndpoints0").with("10");
        fill("#totalpoints0").with("20");
        fill("#earndpoints1").with("10");
        fill("#totalpoints1").with("20");
        fill("#earndpoints2").with("10");
        fill("#totalpoints2").with("20");
        fill("#earndpoints3").with("10");
        fill("#totalpoints3").with("20");
        click("#submit_eval");

//        await().atMost(5,TimeUnit.SECONDS).until("#open_assignment").areDisplayed();
        await().untilPage().isLoaded();
        click("#tab_correction");
        await().atMost(5,TimeUnit.SECONDS).until("#open_assignment").isPresent();
        click("#open_assignment");
        await().atMost(10,TimeUnit.SECONDS).until("#eval_result").areDisplayed();
        assertThat(find("#eval_result").getText()).isEqualTo("40.0/80.0");


    }
    @Test
    public void l_testDeleteStudent(){
        String url="http://localhost:9000/students/Gao/WS2016/LocalLectureTest";
        StudentSignin();
        goTo(url);
        await().atMost(5,TimeUnit.SECONDS).until("#locallecture_deletestudent").isPresent();
        click("#locallecture_deletestudent");
        await().atMost(5,TimeUnit.SECONDS).until("#locallecture_addstudent").areDisplayed();
        assertThat(findFirst("#locallecture_addstudent").isDisplayed());
    }

    @Test
    public void m_testMessage(){
        String url="http://localhost:9000/students/Gao/WS2016/RemoteLectureTest";
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
