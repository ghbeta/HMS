package utils;

import play.Logger;

/**
 * Created by Hao on 2016/1/16.
 */
public class PushEvaluation {

    public static void LocalLectureGitEvaluation(String repoaddress){
        String[] addresspart=repoaddress.split("/");
        Logger.debug("reponame is"+addresspart[3]);
    }
}
