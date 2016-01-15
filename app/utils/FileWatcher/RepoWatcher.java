package utils.FileWatcher;

import play.Logger;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Created by Hao on 2016/1/15.
 */
public class RepoWatcher implements Runnable {
    public WatchService watchService;

    public RepoWatcher(WatchService repowatcher){
        this.watchService=repowatcher;
    }
    @Override
    public void run() {
       try{
           WatchKey key = watchService.take();
           Logger.debug("start key is valid "+(key==null));
           while(key!=null){
               for (WatchEvent event:key.pollEvents()){
                   //Logger.debug("change file system detect"+" "+event.kind().name()+" " +event.context());

                   Path dir=(Path)key.watchable();
                   Logger.debug("change detected "+dir.toString());
                   break;
               }
               //WatchKey key = watchService.take();

//todo filter based on the url
               //key.pollEvents();
               key.reset();

              key=watchService.take();
           }

       }catch (Exception e){
           Logger.debug(e.getMessage());
       }


    }
}
