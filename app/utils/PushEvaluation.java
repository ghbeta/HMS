package utils;

import models.Semesteruser;
import models.User;
import play.Logger;

/**
 * Created by Hao on 2016/1/16.
 */
public class PushEvaluation {

    public static void LocalLectureGitEvaluation(String repoaddress){
        String[] addresspart=repoaddress.split("/");
        Logger.debug("reponame is"+addresspart[4]);
        String userinformation=addresspart[4];
        String[] informationpart=userinformation.split("_");
        Logger.debug("semester is "+informationpart[0]);
        Logger.debug("Lecture name is "+informationpart[1]);
        Logger.debug("user id is "+informationpart[2].replace(".git",""));
        String userid=informationpart[2].replace(".git","");
        Semesteruser semesteruser=Semesteruser.getSemesteruserfomrUser(informationpart[0], User.findById(userid,"global"));
        Logger.debug(semesteruser.lastname);
    }
}
