
import models.*;
import org.h2.tools.Server;
import play.*;
import play.Application;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import utils.CreateAdmin;
import utils.FileWatcher.RepoWatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static utils.CreateDB.createServer;
import static utils.FileWatcher.InitWatchService.getWatchService;
//import static utils.FileWatcher.InitWatchService.registerALL;
import static utils.FileWatcher.InitWatchService.setWatchService;
import static utils.RepoManager.AccessChangerforEvaluation;

/**
 * Created by Hao on 2015/6/15.
 */
public class Global extends GlobalSettings{
    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Logger.info("Application started....");
        Logger.info("starting file watch service");
        try {
            setWatchService();
            RepoWatcher repoWatcher = new RepoWatcher(getWatchService());
            Thread watchThread = new Thread(repoWatcher, "repoWatcherThread");
            watchThread.start();
            //Path toWatch= Paths.get(System.getProperty("user.home"),"repositories","WS2016_LocalLectureTest_7788414.git","logs","refs","heads");
            //Path toWatch= Paths.get(System.getProperty("user.home"),"repositories");
            //Path toWatch= Paths.get(System.getProperty("user.home"));
            //toWatch.register(getWatchService(), ENTRY_MODIFY);
            //registerALL(toWatch);

        } catch (IOException e) {
            Logger.debug(e.getMessage());
        }

        Logger.debug("watch thread started");
        List<Class> entity = new ArrayList<Class>();
        entity.add(User.class);
        entity.add(Token.class);
        entity.add(Semester.class);
        entity.add(SSH.class);
        entity.add(RepoToWatch.class);

        List<Class> entity1 = new ArrayList<Class>();
        entity1.add(Semesteruser.class);
        entity1.add(Assignment.class);
        //entity1.add(Exercise.class);
        entity1.add(Lecture.class);
        entity1.add(Message.class);
        entity1.add(Repo.class);
        entity1.add(Evaluation.class);
        entity1.add(Handin.class);
        entity1.add(ForumThread.class);
        entity1.add(ForumPost.class);
        entity1.add(Conversation.class);

        try {
            Server h2server = Server.createTcpServer("-tcpAllowOthers");
            h2server.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Path p= Paths.get(System.getProperty("user.home"), "data_dynamic", name + ".h2.db");
//        Path database=Paths.get(System.getProperty("user.home"), "data_dynamic");
//        File folder = new File(database.toUri());
//        File[] listofFiles = folder.listFiles();
//        for (int i = 0; i < listofFiles.length; i++) {
//
//            Logger.info("File " + listofFiles[i].getName());
//
//
//
//        }
        createServer("global", entity);
        if (CreateAdmin.createadmin()) {
            List<Semester> database = null;
            try {
                database = Semester.getallsemester();
            } catch (Exception e) {
                database = null;
            }
            if (database != null) {
                for (int i = 0; i < database.size(); i++) {
                    createServer(database.get(i).semester, entity1);
                }
            }
            //createServer("SS2016", entity1);
            //createServer("WS2016", entity1);
        }
        List<RepoToWatch> allreponames = RepoToWatch.findAllReponame();
        if (allreponames.size() > 0) {
            for (RepoToWatch repoWatcher : allreponames) {
                Path toWatch = Paths.get(System.getProperty("user.home"), "repositories", repoWatcher.reponame, "refs", "heads");
                Logger.warn(toWatch.toString() + " was added to watch");
                try {
                    toWatch.register(getWatchService(), ENTRY_MODIFY);
                } catch (IOException e) {
                    Logger.debug(e.getMessage());
                    //e.printStackTrace();
                }
            }
        }

        Akka.system().scheduler().schedule(
                Duration.create(ExcutionDelay(0, 0), TimeUnit.SECONDS),
                Duration.create(7, TimeUnit.DAYS),
                new Runnable(){
                    @Override
                    public void run() {
                        Logger.info("start RepoToWatch database clean Job");
                        List<RepoToWatch> allrepos=RepoToWatch.findAllReponame();
                        if(allrepos.size()>0){
                            for(RepoToWatch repo : allrepos){
                                String[] informationpart=repo.reponame.split("_");
                                String lecturename=informationpart[1];
                                String semester=informationpart[0];
                                Lecture lecture= Lecture.getlecturebyname(lecturename,semester);
                                if(lecture!=null&&!lecture.isExpired()){
                                    String userid=informationpart[2].replace(".git","");
                                    String reponame =repo.reponame;
                                    if(!lecture.localrepo){
                                    AccessChangerforEvaluation(userid,reponame.replace(".git",""),true);
                                    }
                                    repo.delete("global");

                                }


                            }
                        }
                    }
                },
                Akka.system().dispatcher()
        );



    }

    public static int ExcutionDelay(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                new DateTime().withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
        ).getSeconds();
    }
}

