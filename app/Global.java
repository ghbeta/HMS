

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import models.User;
import play.*;
import play.Application;

/**
 * Created by Hao on 2015/6/15.
 */
public class Global extends GlobalSettings{
    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Logger.info("Application started....");
        createServer("global");
    }

    public static void createServer(String name) {
        ServerConfig config = new ServerConfig();
        config.setName(name);

// Define DataSource parameters
        DataSourceConfig postgresDb = new DataSourceConfig();
        postgresDb.setDriver("org.h2.Driver");
        postgresDb.setUsername("hms");
        postgresDb.setPassword("test");
        postgresDb.setUrl("jdbc:h2:file:~/data/playdb");
        postgresDb.setHeartbeatSql("select count(*) from t_one");

        config.setDataSourceConfig(postgresDb);

// specify a JNDI DataSource
// config.setDataSourceJndiName("someJndiDataSourceName");

// set DDL options...
        config.setDdlGenerate(true);
        config.setDdlRun(true);

        config.setDefaultServer(false);
        config.setRegister(false);


// automatically determine the DatabasePlatform
// using the jdbc driver
// config.setDatabasePlatform(new PostgresPlatform());

// specify the entity classes (and listeners etc)
// ... if these are not specified Ebean will search
// ... the classpath looking for entity classes.
        config.addClass(User.class);
        //config.addClass(Customer.class);


// specify jars to search for entity beans
        config.addJar("someJarThatContainsEntityBeans.jar");

// create the EbeanServer instance
        EbeanServer server = EbeanServerFactory.create(config);

    }
}
