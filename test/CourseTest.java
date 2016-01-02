import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Created by Hao on 2016/1/2.
 */
public class CourseTest extends FluentTest {

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

    public void b_createAssignment(){
        String url="http://localhost:9000/admin/123/WS2016/LocalLectureTest";
    }
}
