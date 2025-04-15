package edu.cit.lingguahey.Entity;

import java.util.List;

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
    private int activityID;

    private String activityName;
    private boolean isDeployed; // e add pa ni sa ERD

    //Connections
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private ClassroomEntity activityClassroom;

    @OneToMany(mappedBy = "activities")
    private List<QuestionEntity> questions;

    //Constructors Getter Setters
    public LiveActivityEntity(){
        super();
    }

    public LiveActivityEntity(String lessonName, boolean isDeployed) {
        this.activityName = lessonName;
        this.isDeployed = isDeployed;
    }

    public int getActivityID() {
        return activityID;
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
