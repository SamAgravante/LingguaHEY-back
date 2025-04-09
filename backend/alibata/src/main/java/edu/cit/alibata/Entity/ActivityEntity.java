package edu.cit.alibata.Entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private int activityId;

    private String activityName;
    private boolean isCompleted;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @OneToMany(mappedBy = "activity")
    private List<QuestionEntity> questions;

    @ManyToMany(mappedBy = "activities")
    private List<UserEntity> users;

    public enum GameType {
        GAME1, GAME2, GAME3
    }

    public ActivityEntity(){
        super();
    }

    public ActivityEntity(String activityName, boolean isCompleted, GameType gameType) {
        this.activityName = activityName;
        this.isCompleted = isCompleted;
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

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
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

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

}
