package controllers.messages;

import play.api.libs.json.Json;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;

/**
 * Created by Hao on 2015/11/14.
 */
public class Messagecontrol extends Controller {

      public static WebSocket<String> messagesockets(){

          return WebSocket.whenReady((in, out) -> {
              // For each event received on the socket,

              in.onMessage(new F.Callback<String>() {
                  @Override
                  public void invoke(String s) throws Throwable {
                      out.write("aaa");


                  }
              });

              // When the socket is closed.
              in.onClose(() -> System.out.println("Disconnected"));

              // Send a single 'Hello!' message
              out.write("Hello!");
          });
      }
}
