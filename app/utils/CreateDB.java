package utils;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import org.h2.tools.Server;
import play.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by Hao on 2015/7/6.
 */
public class CreateDB {


    public static void createServer(String name, List<Class> entity) {

        ServerConfig config = new ServerConfig();
        config.setName(name);
       //config.loadFromProperties();
// Define DataSource parameters
        DataSourceConfig postgresDb = new DataSourceConfig();
        postgresDb.setDriver("org.h2.Driver");
        postgresDb.setUsername("hms");
        postgresDb.setPassword("test");
        postgresDb.setUrl("jdbc:h2:tcp://localhost/~/data_dynamic/"+name);
        //postgresDb.setUrl("jdbc:h2:file:~/data_dynamic/"+name);
//       // postgresDb.setHeartbeatSql("select count(*) from t_one");
//        //postgresDb.loadSettings("global");

//
        config.setDataSourceConfig(postgresDb);
        Path p= Paths.get(System.getProperty("user.home"),"data_dynamic",name+".h2.db");
        Logger.info(p.toString());
        File f = p.toFile();
// specify a JNDI DataSource
// config.setDataSourceJndiName("someJndiDataSourceName");

// set DDL options...
        if(f.exists()){
            Logger.info("file is there");
        config.setDdlGenerate(false);
        config.setDdlRun(false);}
        else
        {Logger.info("wrong universe");
            config.setDdlGenerate(true);
            config.setDdlRun(true);
        }



        config.setUpdateChangesOnly(true);
        config.setDefaultServer(false);
        config.setRegister(true);


// automatically determine the DatabasePlatform
// using the jdbc driver
// config.setDatabasePlatform(new PostgresPlatform());

// specify the entity classes (and listeners etc)
// ... if these are not specified Ebean will search
// ... the classpath looking for entity classes.

       for(int i=0;i<entity.size();i++){
        config.addClass(entity.get(i));
           Logger.info(entity.get(i).getName());}
        //config.addClass(Customer.class);

// specify jars to search for entity beans
        //config.addJar("someJarThatContainsEntityBeans.jar");

// create the EbeanServer instance

        EbeanServer server =
                EbeanServerFactory.create(config);

    }
}

