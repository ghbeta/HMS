package models;

import play.db.ebean.Model;

import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.util.Date;
import java.util.List;

/**
 * Created by Hao on 2015/11/4.
 */
public class Handin extends Model {

    @ManyToOne
    Semesteruser student;

    Lecture lecture;

    Assignment assignment;

    @Version
    private Long version;

    public float totalpoints;

    public float earndpoints;

    public Date handin;

    public boolean ishandin;

    public boolean isvalid;

    public void setishandin(){
        ishandin=(assignment.deadline !=null&&handin.before(assignment.deadline));
    }

    public boolean getishandin(){
        return ishandin;
    }

    public void setIsvalid(){
        float percentage=earndpoints/assignment.totalpoints;
        isvalid= (percentage>=lecture.requiredpercentfovalidassignment);
    }

        public void setTotalpoints(){
        totalpoints=0f;
        for(int i=0;i<exercises.size();i++){
            totalpoints=totalpoints+exercises.get(i).totalpoints;
        }
    }

    public void setEarndpoints(){
        earndpoints=0f;
        for(int i=0;i<exercises.size();i++){
            earndpoints=earndpoints+exercises.get(i).earndpoints;
        }
    }
}
