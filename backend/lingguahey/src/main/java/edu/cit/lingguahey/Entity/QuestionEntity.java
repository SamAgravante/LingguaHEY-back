package edu.cit.lingguahey.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private int questionId;

    private String questionDescription;
    private String questionText;
    @Lob
    @Column(name = "question_image", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] questionImage;

    @OneToMany(mappedBy = "question")
    @JsonManagedReference(value = "question-choices")
    private List<ChoiceEntity> choices;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "score_id")
    @JsonManagedReference(value = "question-score")
    private ScoreEntity score;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @JsonBackReference(value = "activity-questions")
    private LessonActivityEntity activity;

    public QuestionEntity(){
        super();
    }

    public QuestionEntity(String questionDescription, String questionText, byte[] questionImage) {
        this.questionDescription = questionDescription;
        this.questionText = questionText;
        this.questionImage = questionImage;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public byte[] getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(byte[] questionImage) {
        this.questionImage = questionImage;
    }

    public List<ChoiceEntity> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceEntity> choices) {
        this.choices = choices;
    }

    public ScoreEntity getScore() {
        return score;
    }

    public void setScore(ScoreEntity score) {
        this.score = score;
    }

    public LessonActivityEntity getActivity() {
        return activity;
    }

    public void setActivity(LessonActivityEntity activity) {
        this.activity = activity;
    }
   
}
