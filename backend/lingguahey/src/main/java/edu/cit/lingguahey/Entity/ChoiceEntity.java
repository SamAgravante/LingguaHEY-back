package edu.cit.lingguahey.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ChoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private int choiceId;

    private String choiceText;
    private boolean isCorrect;
    private Integer choiceOrder;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference(value = "question-choices")
    private QuestionEntity question;

    public ChoiceEntity() {
        super();
    }

    public ChoiceEntity(String choiceText, boolean isCorrect) {
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getChoiceOrder() {
        return choiceOrder;
    }

    public void setChoiceOrder(Integer choiceOrder) {
        this.choiceOrder = choiceOrder;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
   
}
