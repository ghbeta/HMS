package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.about;

/**
 * Created by Hao on 2015/10/1.
 */
public class About extends Controller {
    public static Result index(){
        return ok(about.render());
    }
}
