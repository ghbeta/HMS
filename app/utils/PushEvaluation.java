package utils;

import models.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.mail.EmailException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import play.Logger;
import play.i18n.Messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

/**
 * Created by Hao on 2016/1/16.
 */
public class PushEvaluation {

    public static void GitEvaluation(String repoaddress){
        String[] addresspart=repoaddress.split("/");
        String userinformation=addresspart[4];
        String[] informationpart=userinformation.split("_");
        String lecturename=informationpart[1];
        String semester=informationpart[0];
        Lecture lecture= Lecture.getlecturebyname(lecturename,semester);
        if(lecture!=null&&lecture.localrepo){
            try {
                LocalLectureGitEvaluation(repoaddress);
            } catch (Exception e) {
                Logger.warn(e.getMessage());
            }
        }
        if(lecture!=null&&!lecture.localrepo){
            try {
               RemoteLectureGitEvaluation(repoaddress);
            } catch (Exception e) {
                Logger.warn(e.getMessage());
            }
        }
    }

    public static void RemoteLectureGitEvaluation(String repoaddress) throws IOException, GitAPIException {
        String[] addresspart=repoaddress.split("/");
        String[] evaluationResult=null;
        String reponame =addresspart[4];

        //Logger.debug("reponame is"+reponame);
        String repopath=repoaddress.replace("/refs/heads","");

        //Logger.debug("repopath is" +repopath);
        String userinformation=addresspart[4];
        String[] informationpart=userinformation.split("_");
        String semester=informationpart[0];
        //Logger.debug("semester is "+semester);
        String lecturename=informationpart[1];
        //Logger.debug("Lecture name is "+lecturename);
        String userid=informationpart[2].replace(".git","");
        //Logger.debug("user id is "+userid);
        Semesteruser semesteruser=null;//Semesteruser.getSemesteruserfomrUser(informationpart[0], User.findById(userid,"global"));
        Semesteruser student=null;
        String assignmentTitle=null;
        Lecture lecture= null;//Lecture.getlecturebyname(lecturename,semester);
        Assignment assignment=null;

        Handin handin=null;
        Evaluation eval=null;//Evaluation.findByLectureAndUser(semester, lecture, semesteruser);
        //Logger.debug(semesteruser.lastname);
        String localrepopath=System.getProperty("user.home")+"/"+"data_dynamic"+"/"+semester+"/"+lecturename+"/"+userid+"/"+reponame.replace(".git","");
        Logger.debug(localrepopath);

        File localPath = new File(localrepopath, "");
        Git git=null;
        Repository repository=null;
        //localPath.delete();
        if(!localPath.exists()) {
            //FileUtils.forceDelete(localPath);
            Logger.debug("not exist should create local repo");
            Logger.debug("Cloning from source "+repopath+"to"+localPath);
            git = Git.cloneRepository()
                    .setURI(repopath)
                    .setDirectory(localPath)
                    .call();
            repository=new FileRepository(localrepopath+"/.git");
        }
        else{
            repository=new FileRepository(localrepopath+"/.git");
            git = new Git(repository);
            Logger.debug("starting merging remote to local");
            git.pull().call();
        }












//        Repository repository=new FileRepository(localrepopath);
//        Git git = new Git(repository);
//        //Logger.debug("starting merging remote to local");
//        git.pull().call();
        Logger.debug("current branch "+repository.getBranch());
        RevWalk walk= new RevWalk(repository);

        ObjectId head=repository.resolve(Constants.HEAD);

        RevCommit headCommit=walk.parseCommit(head);

        String committerEmailofHead=headCommit.getAuthorIdent().getEmailAddress();
        String committerMsgofHead=headCommit.getFullMessage().split("_")[0];
        Logger.debug("committer email for head "+committerEmailofHead);
        semesteruser=Semesteruser.findByEmail(committerEmailofHead,semester);
        if(semesteruser!=null&&semesteruser.roles.equals(UserRoll.Students.toString())){
            if(committerMsgofHead.toLowerCase().contains("assignment")){
            lecture=Lecture.getlecturebyname(lecturename,semester);
            assignmentTitle=capitalize(committerMsgofHead);
            assignment=Assignment.findByLectureAndName(semester,lecturename,assignmentTitle);
            handin=Handin.getHandinofassignmentofstudentinlecture(semester,lecture,semesteruser,assignment);
            if(handin==null&&assignment!=null){

            handin= new Handin();
            handin.student=semesteruser;
            handin.lecture=lecture;
            handin.assignment=assignment;
            handin.handin=new Date();
            handin.setishandin();
            handin.isevaluated=false;
            //assignment.handin=new Date();
            //assignment.setishandin();
            //handin.
            if(handin.ishandin){
            handin.save(semester);
            Logger.warn("remove student from repository until handin is corrected");
            RepoManager.AccessChangerforEvaluation(semesteruser.id,reponame.replace(".git",""),true);

            }
            }
            }
        }

        if(semesteruser!=null&&(semesteruser.roles.equals(UserRoll.Teachers.toString())||semesteruser.roles.equals(UserRoll.Assistants.toString()))){
            ObjectId beforehead=repository.resolve("HEAD^");
            RevCommit beforeheadCommit=walk.parseCommit(beforehead);
            String commiterbeforehead=beforeheadCommit.getAuthorIdent().getEmailAddress();
            String committerMsgofbeforeHead=beforeheadCommit.getFullMessage().split("_")[0];
            if(!commiterbeforehead.equals(semesteruser.email)&&committerMsgofbeforeHead.toLowerCase().contains("assignment")){
                lecture=Lecture.getlecturebyname(lecturename,semester);
                assignmentTitle=capitalize(committerMsgofbeforeHead);
                assignment=Assignment.findByLectureAndName(semester,lecturename,assignmentTitle);
                handin=Handin.getHandinofassignmentofstudentinlecture(semester,lecture,semesteruser,assignment);
                eval=Evaluation.findByLectureAndUser(semester, lecture, semesteruser);
                Logger.debug("ready to compare");
                ObjectId newhead = repository.resolve("HEAD^{tree}");
                ObjectId oldHead = repository.resolve("HEAD^^{tree}");
                Logger.debug("Printing diff between tree: " + oldHead + " and " + head);
                try (ObjectReader reader = repository.newObjectReader()) {
                    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                    oldTreeIter.reset(reader, oldHead);
                    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                    newTreeIter.reset(reader, newhead);

                    List<DiffEntry> diffs = git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();
                    ByteArrayOutputStream changes = new ByteArrayOutputStream();
                    DiffFormatter formatter = new DiffFormatter(changes);
                    formatter.setRepository(repository);
                    formatter.format(oldHead, newhead);
                    String diffresult = changes.toString();
                    evaluationResult=CommitParser(diffresult);
                    diffresult=null;
                    changes.reset();
                }
                catch(Exception e){
                    Logger.warn(e.getMessage());
                }
                student=Semesteruser.getSemesteruserfomrUser(informationpart[0], User.findById(userid,"global"));
                boolean updated=updateHandinResult(semester,handin,evaluationResult,eval,lecture,student,semesteruser,git);
                if(updated){
                    Logger.warn("readded user to repository");
                    RepoManager.AccessChangerforEvaluation(student.id,reponame.replace(".git",""),false);
                }
            }
        }

    }

    public static void LocalLectureGitEvaluation(String repoaddress) throws GitAPIException, IOException {
        String[] addresspart=repoaddress.split("/");
        String[] evaluationResult=null;
        String reponame =addresspart[4];

        //Logger.debug("reponame is"+reponame);
        String repopath=repoaddress.replace("/refs/heads","");

        //Logger.debug("repopath is" +repopath);
        String userinformation=addresspart[4];
        String[] informationpart=userinformation.split("_");
        String semester=informationpart[0];
        //Logger.debug("semester is "+semester);
        String lecturename=informationpart[1];
        //Logger.debug("Lecture name is "+lecturename);
        String userid=informationpart[2].replace(".git","");
        //Logger.debug("user id is "+userid);
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(informationpart[0], User.findById(userid,"global"));
        Semesteruser teacher=null;
        Lecture lecture= Lecture.getlecturebyname(lecturename,semester);
        Assignment assignment=null;

        Handin handin=null;
        Evaluation eval=Evaluation.findByLectureAndUser(semester, lecture, semesteruser);
        //Logger.debug(semesteruser.lastname);
        String localrepopath=System.getProperty("user.home")+"/"+"data_dynamic"+"/"+semester+"/"+lecturename+"/"+userid+"/"+reponame.replace(".git","/.git");
        Logger.debug(localrepopath);
        Repository repository=new FileRepository(localrepopath);
        Git git = new Git(repository);
        //Logger.debug("starting merging remote to local");
        git.pull().call();
        Logger.debug("current branch "+repository.getBranch());
        RevWalk walk= new RevWalk(repository);

        ObjectId head=repository.resolve(Constants.HEAD);

        RevCommit headCommit=walk.parseCommit(head);

        String committerofhead=headCommit.getAuthorIdent().getName();


        Logger.debug("committer for head "+committerofhead);
        if(committerofhead!=null&&!committerofhead.isEmpty()&&!committerofhead.equals("hms")) {
            ObjectId beforehead=repository.resolve("HEAD^");
            RevCommit beforeheadCommit=walk.parseCommit(beforehead);
            String commiterbeforehead=beforeheadCommit.getAuthorIdent().getName();
            Logger.warn("commite before head " + commiterbeforehead);
            if(commiterbeforehead.equals("hms")){
            Logger.debug("ready to compare");
            ObjectId newhead = repository.resolve("HEAD^{tree}");
            ObjectId oldHead = repository.resolve("HEAD^^{tree}");
            Logger.debug("Printing diff between tree: " + oldHead + " and " + head);
            try (ObjectReader reader = repository.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, newhead);

                List<DiffEntry> diffs = git.diff()
                        .setNewTree(newTreeIter)
                        .setOldTree(oldTreeIter)
                        .call();
                ByteArrayOutputStream changes = new ByteArrayOutputStream();
                DiffFormatter formatter = new DiffFormatter(changes);
                formatter.setRepository(repository);
                formatter.format(oldHead, newhead);
                String diffresult = changes.toString();
                evaluationResult = CommitParser(diffresult);
                diffresult = null;
                changes.reset();

                String assignmentTitle = beforeheadCommit.getFullMessage().split("_")[0];
                Logger.warn("assignment title" + assignmentTitle);
                if (assignmentTitle.contains("Assignment")) {
                    assignment = Assignment.findByLectureAndName(semester, lecturename, assignmentTitle);

                    handin = Handin.getHandinofassignmentofstudentinlecture(semester, lecture, semesteruser, assignment);
                } else {
                    Logger.warn("handin information not found");
                }
                //}
            } catch (Exception e) {
                Logger.warn(e.getMessage());
            }
                teacher=Semesteruser.findByEmail(headCommit.getAuthorIdent().getEmailAddress(),semester);
                updateHandinResult(semester,handin,evaluationResult,eval,lecture,semesteruser,teacher,git);
        }
        }




    }

    public static boolean updateHandinResult(String semester,Handin handin,String[] result,Evaluation eval,Lecture lecture, Semesteruser student,Semesteruser teacher,Git git) {
        if(handin!=null&&result!=null){
            float earndpoints=Float.valueOf(result[0]);
            handin.comments= result[1];
            handin.setEarndpoints(earndpoints);
            handin.setTotalpoints();
            handin.setIsvalid();
            handin.isevaluated=true;
            if(teacher!=null){
                handin.marker=teacher;
            }
            handin.update(semester);
            Logger.warn("handin update success");
            eval.setPerformance(semester,lecture,student);
            eval.update(semester);
            try {
                Logger.debug("Merging Correction into Local repository and sending email");
                git.pull().call();
                sendMailForEvaluation(student, handin);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }

    static void sendMailForEvaluation(Semesteruser semesteruser, Handin handin) throws EmailException,MalformedURLException {
        String subject = Messages.get("mail.evaluation.done.subject");
        String message= Messages.get("email.handin.message")+handin.assignment.title+" "+handin.lecture.courseName+Messages.get("email.handin.isautoevaluated")+"\n"+handin.comments;
        Mail.Envelop envelop = new Mail.Envelop(subject, message, semesteruser.email);
        Mail.sendMail(envelop);
    }


    public static String[]  CommitParser(String diffs) throws IOException {
        BufferedReader bufReader = new BufferedReader(new StringReader(diffs));
        String[] evaluResult=new String[2];
        String line=null;
        float earndpoints=0;
        Pattern getnumber1 = Pattern.compile("((//)([ ]*)([\\+\\-])?(\\d+(\\.\\d+)?)(?![\\d.x]))|([\\+\\-])?(\\d+(\\.\\d+)?)(?![\\d.x])$");
        Pattern getnumber2 = Pattern.compile("([\\+\\-])?(\\d+(\\.\\d+)?)(?![\\d.x])");
        Matcher firstmatch=null;
        Matcher finalmatch=null;
        while ((line=bufReader.readLine())!=null){
            if(line.matches("^(\\+)([^\\+]).*$")){
                Logger.debug("start match each line "+line);
               firstmatch=getnumber1.matcher(line);
                firstmatch.find();
                String point=firstmatch.group();
                Logger.debug("first match is "+point);
               if(point.contains("//")){
                   finalmatch=getnumber2.matcher(point);
                   finalmatch.find();
                   point=finalmatch.group();
                   Logger.debug("final match is "+point);
               }
                earndpoints=earndpoints+Float.valueOf(point);
            }
        }
        evaluResult[0]=earndpoints+"";
        evaluResult[1]=diffs;
        Logger.warn("points from commit "+evaluResult[0]);
        return evaluResult;
    }
}
