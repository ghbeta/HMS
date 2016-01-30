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
public class RepoToWatch extends Model {
    @Id
    public String reponame;

    public static RepoToWatch findByReponame(String reponame){
        return getServer("global").find(RepoToWatch.class).where().eq("reponame",reponame).findUnique();
    }

    public static List<RepoToWatch> findAllReponame(){
        return getServer("global").find(RepoToWatch.class).findList();
    }

}
