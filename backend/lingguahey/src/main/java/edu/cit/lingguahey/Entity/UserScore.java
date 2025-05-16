package edu.cit.lingguahey.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
    
@Getter
@Setter
@Entity
@Table(name = "user_scores")
public class UserScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "score_id"/*, nullable = false*/)
    private ScoreEntity scoreEntity;

    @Column(nullable = false)
    private int score;

    public UserScore() {
        super();
    }

    public UserScore(UserEntity user, QuestionEntity question, int score) {
        this.user = user;
        this.question = question;
        this.score = score;
    }
}
