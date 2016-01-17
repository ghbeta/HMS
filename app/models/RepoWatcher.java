package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

import static com.avaje.ebean.Ebean.getServer;

/**
 * Created by Hao on 2016/1/17.
 */
@Entity
@Table(name="repositories")
public class RepoWatcher extends Model {
    @Id
    public String reponame;

    public static RepoWatcher findByReponame(String reponame){
        return getServer("global").find(RepoWatcher.class).where().eq("reponame",reponame).findUnique();
    }

    public static List<RepoWatcher> findAllReponame(){
        return getServer("global").find(RepoWatcher.class).findList();
    }

}
