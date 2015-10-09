package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * Created by Hao on 2015/10/8.
 */
public class Semesteruser extends Abstractuser {
    @Constraints.Required
    @Formats.NonEmpty
    public String semester;

    @ManyToMany(cascade= CascadeType.ALL)
    public List<Lecture> lectures;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "editor")
    public Set<Assignment> assignments;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "owner")
    public Set<Repo> repos;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "marker")
    public Set<Exercise> exercises;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sender")
    public Set<Message> messages;
}
