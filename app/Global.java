

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import models.Token;
import models.User;
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
            createServer("global", entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

