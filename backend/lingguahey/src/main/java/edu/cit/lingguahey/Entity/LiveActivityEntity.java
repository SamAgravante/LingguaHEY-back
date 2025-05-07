package edu.cit.lingguahey.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class LiveActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private int activityId;

    private String activityName;
    private boolean isDeployed; // e add pa ni sa ERD

    //Connections
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    //@JsonBackReference(value = "classroom-liveactivities")
    @JsonIgnore
    private ClassroomEntity activityClassroom;

    @OneToMany(mappedBy = "liveActivity")
    @JsonManagedReference(value = "liveactivity-questions")
    private List<QuestionEntity> questions;

    @OneToMany(mappedBy = "liveActivity")
    @JsonManagedReference(value = "live-users")
    private List<UserEntity> userActivities;

    //Constructors Getter Setters
    public LiveActivityEntity(){
        super();
    }

    public LiveActivityEntity(String activityName, boolean isDeployed) {
        this.activityName = activityName;
        this.isDeployed = isDeployed;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isDeployed() {
        return isDeployed;
    }

    public void setDeployed(boolean isCompleted) {
        this.isDeployed = isCompleted;
    }


    public List<QuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionEntity> questions) {
        this.questions = questions;
    }

    public ClassroomEntity getClassroom() {
        return activityClassroom;
    }

    public void setClassroom(ClassroomEntity classroom) {
        this.activityClassroom = classroom;
    }
}
