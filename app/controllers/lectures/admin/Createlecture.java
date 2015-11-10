package controllers.lectures.admin;

import Permission.Securedteacher;
import models.*;
import play.api.data.Forms;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.lectures.admin.createlectureform;
import views.html.lectures.admin.lecturehome;


import static play.data.Form.form;
import static utils.CreateDB.createServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hao on 2015/10/10.
 */
public class Createlecture extends Controller {
   public static class LectureRemoteRegeister{
       @Constraints.Required
       public String yearprefix;

       @Constraints.Required
       public String year;

       @Constraints.Required
       public String coursename;


       public String localrepo;

       @Constraints.Required
       public Date closingdate;

       public String description;

   }

    public static class LectureRegister {


        @Constraints.Required
        public String yearprefix;

        @Constraints.Required
        public String year;

        @Constraints.Required
        public String coursename;


        public String localrepo;

        @Constraints.Required
        public Date closingdate;

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


        public String validate() {
            if(isblank(yearprefix)){
                //return "yearprefix is required ";
                return null;
            }
            if(isblank(year)){
                //return "year is required ";
                return null;
            }
            if(isblank(coursename)){
                //return "coursename is required ";
                return null;
            }
            if(isblank(closingdate)){
               // return "closingdate is required ";
                return null;
            }
            if(isblank(totalassignment)){
               // return "totalassignment is required ";
                return null;
            }
            if(isblank(optionalassigment)){
               // return "optionalassigment is required ";
                return null;
            }
            if(isblank(numberofvalidassignment)){
                //return "numberofvalidassignment is required ";
                return null;
            }
            if(isblank(percentageforvalidassignment)){
                //return "percentageforvalidassignment is required ";
                return null;
            }
            if(isblank(percentageforexam)){
                //return "percentageforexam is required ";
                return null;
            }
            return null;
        }

        public boolean isblank(Object t){
            return t==null;
        }
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result createlectureform() {
        User currentuser = User.findByEmail(request().username(), "global");
        return ok(createlectureform.render(currentuser));
    }

    @Security.Authenticated(Securedteacher.class)
    public static Result createlecture() {

        Form<LectureRegister> createlectureForm = form(LectureRegister.class).bindFromRequest();
        Form<LectureRemoteRegeister> createlectureremoteForm=form(LectureRemoteRegeister.class).bindFromRequest();
        //System.out.println(createlectureForm);
        if(!createlectureForm.hasErrors()) {
            String semester = createlectureForm.get().yearprefix + createlectureForm.get().year;
            User globaluser=User.findByEmail(ctx().session().get("email"),"global");

            if (Semester.findsemester(semester) == null) {
                List<Class> entity = new ArrayList<Class>();
                entity.add(Semesteruser.class);
                entity.add(Assignment.class);
                entity.add(Exercise.class);
                entity.add(Lecture.class);
                entity.add(Message.class);
                entity.add(Repo.class);
                entity.add(Evaluation.class);
                entity.add(Handin.class);
                entity.add(ForumPost.class);
                entity.add(ForumThread.class);
                createServer(semester, entity);
                Semester addsemester = new Semester();
                addsemester.semester = semester;
                addsemester.save("global");
            }
            Lecture lecture = new Lecture();
            lecture.semester = semester;
            lecture.courseName = createlectureForm.get().coursename;
            if (createlectureForm.get().description != null) {
                lecture.desription = createlectureForm.get().description;
            } else {
                lecture.desription = Messages.get("lecture.form.des.none");
            }
            if (createlectureForm.get().localrepo == null) {
                lecture.localrepo = false;
                lecture.totalassignment=1;
                lecture.optionalassignments = 0;
                lecture.requriednumberofvalidassignment = 0;
                lecture.requiredpercentfovalidassignment = 0;
                lecture.minimumPercentageForExamination = 0;
            } else if (createlectureForm.get().localrepo.equals("true")) {
                lecture.localrepo = true;
                lecture.totalassignment = createlectureForm.get().totalassignment;
                lecture.optionalassignments = createlectureForm.get().optionalassigment;
                lecture.requriednumberofvalidassignment = createlectureForm.get().numberofvalidassignment;
                lecture.requiredpercentfovalidassignment = createlectureForm.get().percentageforvalidassignment;
                lecture.minimumPercentageForExamination = createlectureForm.get().percentageforexam;
            }

            lecture.closingdate = createlectureForm.get().closingdate;


            Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,globaluser);
            lecture.lasteditor = semesteruser;
            if (!lecture.attendent.contains(lecture.lasteditor)) {
                lecture.attendent.add(lecture.lasteditor);
            }
//        if(lecture.totalassignment>0){
//        for(int i=1;i<=lecture.totalassignment;i++){
//            Assignment assignment=new Assignment();
//            assignment.title=Messages.get("lecture.homework")+i;
//            assignment.semester=semester;
//            assignment.lecture=lecture;
//            lecture.assignments.add(assignment);
//        }}
//        if(lecture.optionalassignments>0) {
//            for (int i = 1; i <= lecture.optionalassignments; i++) {
//                Assignment assignment = new Assignment();
//                assignment.title = Messages.get("lecture.homework.optional") + i;
//                assignment.semester = semester;
//                assignment.lecture = lecture;
//                lecture.assignments.add(assignment);
//            }
//        }
            try {
                lecture.save(lecture.semester);
                flash("success", Messages.get("lecture.create.success"));
                //return redirect(lecturehome.render(globaluser,semesteruser,lecture));
                return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(globaluser.lastname, semester, lecture.courseName));
            } catch (Exception e) {
                flash("danger", Messages.get("lecture.create.fail"));
                return badRequest(lecturehome.render(globaluser, semesteruser, lecture));
            }

        }

        if(!createlectureremoteForm.hasErrors()){
            String semester = createlectureremoteForm.get().yearprefix + createlectureremoteForm.get().year;
            User globaluser=User.findByEmail(ctx().session().get("email"),"global");

            if (Semester.findsemester(semester) == null) {
                List<Class> entity = new ArrayList<Class>();
                entity.add(Semesteruser.class);
                entity.add(Assignment.class);
                entity.add(Exercise.class);
                entity.add(Lecture.class);
                entity.add(Message.class);
                entity.add(Repo.class);
                entity.add(Evaluation.class);
                entity.add(Handin.class);
                entity.add(ForumPost.class);
                entity.add(ForumThread.class);
                createServer(semester, entity);
                Semester addsemester = new Semester();
                addsemester.semester = semester;
                addsemester.save("global");
            }
            Lecture lecture = new Lecture();
            lecture.semester = semester;
            lecture.courseName = createlectureremoteForm.get().coursename;
            if (createlectureremoteForm.get().description != null) {
                lecture.desription = createlectureremoteForm.get().description;
            } else {
                lecture.desription = Messages.get("lecture.form.des.none");
            }
            if (createlectureremoteForm.get().localrepo == null) {
                lecture.localrepo = false;
                lecture.totalassignment=1;
                lecture.optionalassignments = 0;
                lecture.requriednumberofvalidassignment = 0;
                lecture.requiredpercentfovalidassignment = 0;
                lecture.minimumPercentageForExamination = 0;
            }

            lecture.closingdate = createlectureremoteForm.get().closingdate;


            Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(semester,globaluser);
            lecture.lasteditor = semesteruser;
            if (!lecture.attendent.contains(lecture.lasteditor)) {
                lecture.attendent.add(lecture.lasteditor);
            }

            try {
                lecture.save(lecture.semester);
                flash("success", Messages.get("lecture.create.success"));
                //return redirect(lecturehome.render(globaluser,semesteruser,lecture));
                return redirect(controllers.lectures.admin.routes.Lecturehome.generatelecturehome(globaluser.lastname, semester, lecture.courseName));
            } catch (Exception e) {
                flash("danger", Messages.get("lecture.create.fail"));
                return badRequest(lecturehome.render(globaluser, semesteruser, lecture));
            }
        }
        else{
            flash("danger", Messages.get("lecture.create.fail"));
            return redirect(routes.Createlecture.createlectureform());
        }
         }

}