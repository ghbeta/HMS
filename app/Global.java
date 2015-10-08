
import models.*;
import org.h2.tools.Server;
import play.*;
import play.Application;

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

        try {
            Server h2server= Server.createTcpServer();
            h2server.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        createServer("global", entity);

    }
}

