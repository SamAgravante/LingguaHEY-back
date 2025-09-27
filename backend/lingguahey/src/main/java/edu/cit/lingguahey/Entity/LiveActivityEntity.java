package edu.cit.lingguahey.Entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    //Connections
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    //@JsonBackReference(value = "classroom-liveactivities")
    @JsonIgnore
    private ClassroomEntity activityClassroom;

    @OneToMany(mappedBy = "liveActivity")
    @JsonManagedReference(value = "liveactivity-questions")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<QuestionEntity> questions;

    @OneToMany(mappedBy = "liveActivity")
    @JsonManagedReference(value = "live-users")
    private List<UserEntity> userActivities;

    public enum GameType {
        GAME1, GAME2, GAME3
    }

    //Constructors Getter Setters
    public LiveActivityEntity(){
        super();
    }

    public LiveActivityEntity(String activityName, boolean isDeployed, GameType gameType) {
        this.activityName = activityName;
        this.isDeployed = isDeployed;
        this.gameType = gameType;
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

    public void setDeployed(boolean isDeployed) {
        this.isDeployed = isDeployed;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
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
    
    public void setUserActivities(List<UserEntity> userActivities) {
        this.userActivities = userActivities;
    }
}
