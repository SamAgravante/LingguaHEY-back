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
public class LessonActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private int lessonID;

    private String lessonName;
    private String category;
    private boolean isCompleted; // e add pa ni sa ERD

    //Relations
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private ClassroomEntity lessonClassroom;

    @OneToMany(mappedBy = "lessons")
    private List<QuestionEntity> questions;

    //Constructors Getter Setters
    public LessonActivityEntity(){
        super();
    }

    public LessonActivityEntity(String lessonName, String category, boolean isCompleted) {
        this.lessonName = lessonName;
        this.category = category;
        this.isCompleted = isCompleted;
    }

    public int getLessonID() {
        return lessonID;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /* 
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
    */

}
