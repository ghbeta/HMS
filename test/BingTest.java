import org.junit.Test;
import play.test.TestBrowser;
import play.libs.F.Callback;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.FIREFOX;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.testServer;
import static play.test.Helpers.running;
import static org.fest.assertions.Assertions.assertThat;


public class BingTest {
    @Test
    public void runInBrowser() {
        running(testServer(3333), FIREFOX, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://www.bing.com");
                browser.fill("#sb_form_q").with("FluentLenium");
                browser.submit("#sb_form_go");
                assertThat(browser.title()).contains("FluentLenium");
//                browser.$("a").click();
//                assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
            }
        });
    }
}