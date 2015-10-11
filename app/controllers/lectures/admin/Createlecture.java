package controllers.lectures.admin;

import Permission.Securedteacher;
import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.lectures.admin.createlectureform;
import static play.data.Form.form;
import java.util.Date;

/**
 * Created by Hao on 2015/10/10.
 */
public class Createlecture extends Controller{

    public static class LectureRegister{

        @Constraints.Required
        public String yearprefix;

        @Constraints.Required
        public String year;

        @Constraints.Required
        public String coursename;

        @Constraints.Required
        public String localrepo;

        @Constraints.Required
        public String closingdate;

        @Constraints.Required
        public int totalassignment;

        @Constraints.Required
        public int optionalassigment;

        @Constraints.Required
        public int numberofvalidassignment;

        @Constraints.Required
        public float percentageforvalidassignment;

        @Constraints.Required
        public float percentageforexam;

        public String description;


        public String validate(){
            //todo maybe some validation later
            return null;
        }

    }
    @Security.Authenticated(Securedteacher.class)
    public static Result createlectureform(){
        User currentuser=User.findByEmail(request().username(),"global");
        return ok(createlectureform.render(currentuser));
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result createlecture(){
        //Form<Login> loginForm = form(Login.class).bindFromRequest();
        //todo mapping the lectureForm to lecture object
        Form<LectureRegister> createlectureForm = form(LectureRegister.class).bindFromRequest();
        System.out.println("Form<LectureRegister>: " + createlectureForm);
        return ok("abc");
    }
}
