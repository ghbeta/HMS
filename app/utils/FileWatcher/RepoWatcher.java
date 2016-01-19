package utils.FileWatcher;

import play.Logger;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static utils.PushEvaluation.LocalLectureGitEvaluation;

import java.nio.file.*;

/**
 * Created by Hao on 2016/1/15.
 */
public class RepoWatcher implements Runnable {
    public WatchService watchService;
    public RepoWatcher(WatchService repowatcher){
        this.watchService=repowatcher;
    }
    //public String lastchanged="";
    @Override
    public void run() {
       try{
           WatchKey key = watchService.take();

           Logger.debug("start key is valid "+(key==null));
           while(key!=null){
               //for (WatchEvent event:key.pollEvents()){
                   //Logger.debug("change file system detect"+" "+event.kind().name()+" " +event.context());
               Logger.debug("change detect");
                   key.pollEvents();
                   Path dir=(Path)key.watchable();
                   if(dir.toString().contains("refs/heads")){
                    LocalLectureGitEvaluation(dir.toString());
                   //Logger.debug("change detected "+dir.toString());//+" "+event.kind().name()+" "+event.context());
                   //lastchanged=dir.toString();
                   }
                   //break;
              //}
               //WatchKey key = watchService.take();

               //key.pollEvents();
               key.reset();

              key=watchService.take();
           }

       }catch (Exception e){
           Logger.debug(e.getMessage());
       }


    }
}
