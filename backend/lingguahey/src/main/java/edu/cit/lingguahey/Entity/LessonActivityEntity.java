package edu.cit.lingguahey.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class LessonActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private int activityId;

    private String lessonName;
    private boolean isCompleted;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @OneToMany(mappedBy = "activity")
    @JsonManagedReference(value = "activity-questions")
    private List<QuestionEntity> questions;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    @JsonBackReference(value = "classroom-activities")
    private ClassroomEntity lessonClassroom;

    @ManyToMany(mappedBy = "activities")
    //@JsonBackReference
    @JsonIgnore
    private List<UserEntity> users;

    public enum GameType {
        GAME1, GAME2, GAME3
    }

    public LessonActivityEntity(){
        super();
    }

    public LessonActivityEntity(String lessonName, boolean isCompleted, GameType gameType) {
        this.lessonName = lessonName;
        this.isCompleted = isCompleted;
        this.gameType = gameType;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String activityName) {
        this.lessonName = activityName;
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
