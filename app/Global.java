
import models.*;
import org.h2.tools.Server;
import play.*;
import play.Application;
import play.api.PlayException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utils.CreateDB.createServer;

/**
 * Created by Hao on 2015/6/15.
 */
public class Global extends GlobalSettings{
    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Logger.info("Application started....");
        List<Class> entity=new ArrayList<Class>();
        entity.add(User.class);
        entity.add(Token.class);
        entity.add(Semester.class);

        List<Class> entity1 = new ArrayList<Class>();
        entity1.add(Semesteruser.class);
        entity1.add(Assignment.class);
        entity1.add(Exercise.class);
        entity1.add(Lecture.class);
        entity1.add(Message.class);
        entity1.add(Repo.class);


        try {
            Server h2server= Server.createTcpServer();
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
        List<Semester> database=null;
        try{
        database=Semester.getallsemester();}
        catch (Exception e){
            database=null;
        }
        if(database!=null){
            for(int i=0;i<database.size();i++){
                createServer(database.get(i).semester,entity1);
            }
        }
        //createServer("SS2016", entity1);
        //createServer("WS2016", entity1);
    }
}

