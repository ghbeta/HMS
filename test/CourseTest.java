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
    }

    public void Signin(){
        goTo("http://localhost:9000");
        fill("#LoginEmail").with("456@456.com");
        fill("#LoginPassword").with("456");
        click("#LoginButton");
        assertThat(find("#UserGroup").getText()).isEqualTo("User Group: Teachers");
    }
    @Test
    public void a_createLocalCourse(){
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
        fill("#datepicker").with("03/10/2016");
        fill("#course_description").with("test course under local mode");
        click("#create_course_button");
        await().atMost(5, TimeUnit.SECONDS);
        assertThat(find(".label-success").getText()).isEqualTo("WS2016");

    }

    @Test
    public void b_createAssignment(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        click("#create_local_homework");
        await().atMost(30, TimeUnit.SECONDS).until("#assignmentModal").areDisplayed();
        fill("#number_exercise").with("4");
        fill("#total_points").with("80");
        fill("#datepicker").with("03/10/2016");
        fill("#upload_file").with(System.getProperty("user.home")+"/Assignment1.txt");
        fill("#additional_info").with("test adding new assignment");
        click("#submit_assignment");
        await().atMost(15, TimeUnit.SECONDS).until("#table_localinfo").isPresent();
        assertThat(find("#table_localinfo").getText()).isEqualTo("test adding new assignment");
    }

    @Test
    public void c_modifyAssignment(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
        Signin();
        goTo(url);
        await().atMost(30,TimeUnit.SECONDS).until("#modify_assignment").isPresent();
        click("#modify_assignment");
        await().atMost(30,TimeUnit.SECONDS).until("#1").areDisplayed();
        fill("#modify_assignment_deadline").with("03/09/2016");
        fill("#modify_assignment_info").with("test modify assignment");
        click("#submit_modify_assignment");
        await().atMost(30,TimeUnit.SECONDS).until("#table_localinfo").isPresent();
        assertThat(find("#table_localinfo").getText()).isEqualTo("test modify assignment");
    }
}
