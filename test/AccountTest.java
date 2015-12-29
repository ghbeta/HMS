import org.fluentlenium.adapter.FluentTest;
import org.junit.Before;
import org.junit.Test;
import play.test.Helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.with;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by Hao on 2015/12/29.
 */
public class AccountTest extends FluentTest {
    @Before
    public void startserver(){
        Helpers.start(testServer(9000));
    }

   @Test
   public void testLogin(){
       goTo("http://localhost:9000");
       fill("#exampleInputEmail3").with("123q123.com");
       //submit("#sb_form_go");
       //assertThat(()).contains("FluentLenium");
   }
}
