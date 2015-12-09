package utils;

/**
 * Created by Hao on 2015/10/18.
 */
public class UploadPath {
    public static String uploadpath(String type,String semester,String lecturename){
        String pathprefix= System.getProperty("user.home")+"/"+"data_dynamic";

        String path=pathprefix+"/"+semester+"/"+lecturename+"/"+type;
        return path;

    }

   public static String localrepopullpath(String semester,String lecturename,String userid,String reponame){
       String pathprefix= System.getProperty("user.home")+"/"+"data_dynamic";
       String path=pathprefix+"/"+semester+"/"+lecturename+"/"+userid+"/"+reponame;
       return path;
   }
}
