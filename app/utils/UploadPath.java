package utils;

/**
 * Created by Hao on 2015/10/18.
 */
public class UploadPath {
    public static String uploadpath(String type,String semester,String lecturename){
        String path= type+"/"+semester+"/"+lecturename;
        return path;

    }

//    public static String downloadpath(String type,String semester,String lecturename){
//        String path= type+"/"+semester+"/"+lecturename+"/";
//        return path;
//    }
}
