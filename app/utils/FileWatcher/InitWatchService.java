package utils.FileWatcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by Hao on 2016/1/15.
 */
public class InitWatchService {
    public static WatchService watchService;
    public static void setWatchService() throws IOException {
        watchService= getDefault().newWatchService();
    }

    public static WatchService getWatchService(){
        return watchService;
    }

    public static void registerALL(final Path start) throws IOException{
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                dir.register(watchService,ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
