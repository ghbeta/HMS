package utils;

import models.Handin;
import models.Lecture;
import models.Semesteruser;
import models.User;
import org.apache.commons.io.output.ByteArrayOutputStream;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hao on 2016/1/16.
 */
public class PushEvaluation {

    public static void LocalLectureGitEvaluation(String repoaddress) throws GitAPIException, IOException {
        String[] addresspart=repoaddress.split("/");

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
        Lecture lecture= Lecture.getlecturebyname(lecturename,semester);
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
        String committerofhead=headCommit.getCommitterIdent().getName();



        Logger.debug("committer for head "+committerofhead);
        if(committerofhead!=null&&!committerofhead.isEmpty()&&!committerofhead.equals("hms")) {
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
                CommitParser(diffresult);
                //Logger.debug("diffresult " + diffresult);
                diffresult=null;
                changes.reset();


            }catch (Exception e){
                Logger.warn(e.getMessage());
            }
        }


    }

    public static void updateFileRepository(String localrepopath) throws IOException, GitAPIException {

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
