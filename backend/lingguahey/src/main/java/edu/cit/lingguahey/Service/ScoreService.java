package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Entity.ScoreEntity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Entity.UserScore;
import edu.cit.lingguahey.Repository.QuestionRepository;
import edu.cit.lingguahey.Repository.ScoreRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.Repository.UserScoreRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChoiceService choiceServ;

    @Autowired
    private UserScoreRepository userScoreRepo;

    // Create and Add Score to Question
    public ScoreEntity setScoreForQuestion(int questionId, int scoreValue) {
        QuestionEntity question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        if (question.getScore() != null) {
            throw new IllegalStateException("Score already exists for this question");
        }
        ScoreEntity score = new ScoreEntity();
        score.setScore(scoreValue);
        score.setQuestion(question);
        question.setScore(score);
        return scoreRepo.save(score);
    }

    // Read All Scores
    public List<ScoreEntity> getAllScoreEntity() {
        return scoreRepo.findAll();
    }

    // Read Single Score
    public ScoreEntity getScoreEntity(int scoreId) {
        return scoreRepo.findById(scoreId).get();
    }

    // Update
    public ScoreEntity putScoreEntity(int scoreId, ScoreEntity newScore) {
        try {
        ScoreEntity score = scoreRepo.findById(scoreId).get();
        score.setScore(newScore.getScore());
        if (score.getQuestion() != null) {
            score.getQuestion().setScore(newScore);
        }
        return scoreRepo.save(score);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Score " + scoreId + " not found!");
        }
    }

    // Update Score for Question
    public ScoreEntity updateScoreForQuestion(int questionId, int newScoreValue) {
        QuestionEntity question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        ScoreEntity score = question.getScore();
        if (score == null) {
            throw new EntityNotFoundException("No score found for the question with ID: " + questionId);
        }
        score.setScore(newScoreValue);
        return scoreRepo.save(score);
    }

    // Delete a ScoreEntity by id
    public String deleteScoreEntity(int scoreId) {
        if (scoreRepo.existsById(scoreId)) {
            List<UserScore> userScores = userScoreRepo.findByScoreEntity_ScoreId(scoreId);
            userScoreRepo.deleteAll(userScores);

            scoreRepo.deleteById(scoreId);
            return "Score " + scoreId + " deleted successfully!";
        } else {
            throw new EntityNotFoundException("Score " + scoreId + " not found!");
        }
    }

    // Give Score to User
    public void awardScoreToUser(int questionId, int userId, int selectedChoiceId) {
        QuestionEntity question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        boolean isCorrectChoice = question.getChoices().stream()
            .anyMatch(choice -> choice.getChoiceId() == selectedChoiceId && choice.isCorrect());
        if (!isCorrectChoice) {
            throw new IllegalArgumentException("The selected choice is incorrect.");
        }

        userScoreRepo.findByUser_UserIdAndQuestion_QuestionId(userId, questionId)
            .ifPresent(existing -> {
                throw new IllegalStateException("User already has a score for this question.");
            });

        UserScore userScore = new UserScore(user, question, question.getScore().getScore());
        userScoreRepo.save(userScore);
    }

    // Give Score to User for Translation Game
    public void awardScoreToUserForTranslationGame(int questionId, int userId, List<Integer> choiceIds) {
        QuestionEntity question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    
        boolean isCorrectChoice = choiceServ.validateTranslationGame(questionId, choiceIds);
        if (!isCorrectChoice) {
            throw new IllegalArgumentException("The selected choices are not in the correct order.");
        }
    
        userScoreRepo.findByUser_UserIdAndQuestion_QuestionId(userId, questionId)
            .ifPresent(existing -> {
                throw new IllegalStateException("User already has a score for this question.");
            });
    
        UserScore userScore = new UserScore(user, question, question.getScore().getScore());
        userScoreRepo.save(userScore);
    }

    // Total Score for User
    @SuppressWarnings("unused")
    public int getTotalScoreForUser(int userId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return userScoreRepo.findByUser_UserId(userId).stream()
            .mapToInt(userScore -> userScore.getScore())
            .sum();
    }
}

