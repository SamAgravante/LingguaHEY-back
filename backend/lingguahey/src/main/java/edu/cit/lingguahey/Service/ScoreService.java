package edu.cit.lingguahey.Service;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Broadcaster.LiveActivityBroadcaster;
import edu.cit.lingguahey.DTO.LiveActivityUpdate;
import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Entity.ScoreEntity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Entity.UserScore;
import edu.cit.lingguahey.Repository.QuestionRepository;
import edu.cit.lingguahey.Repository.ScoreRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.Repository.UserScoreRepository;
import edu.cit.lingguahey.model.LeaderboardEntry;
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

    @Autowired
    private LiveActivityBroadcaster liveActivityBroadcaster;

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

    // Adds bonus points to user score from time constraints
    private int calculateTimeBonus(double timeTakenSeconds) {
        if (timeTakenSeconds < 3) {
            return 2;
        } 
        else if (timeTakenSeconds < 5) {
            return 1;
        }
        return 0;
    }


    // Give Score to User
    @Transactional
    public void awardScoreToUser(int questionId, int userId, int selectedChoiceId, double timeTakenSeconds) {
        QuestionEntity question = questionRepo.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        userScoreRepo.findByUser_UserIdAndQuestion_QuestionId(userId, questionId)
                .ifPresent(existing -> {
                    throw new IllegalStateException("User already has a score for this question. Score: " + existing.getScore());
                });

        boolean isCorrectChoice = question.getChoices().stream()
                .anyMatch(choice -> choice.getChoiceId() == selectedChoiceId && choice.isCorrect());
        
        if (!isCorrectChoice) {
            throw new IllegalArgumentException("The selected choice is incorrect."); 
        }

        int baseScore = question.getScore().getScore();
        int timeBonus = calculateTimeBonus(timeTakenSeconds);
        
        int awardedScore = baseScore + timeBonus;


        UserScore userScore = new UserScore(user, question, awardedScore);
        userScoreRepo.save(userScore);
        
        if (question.getLiveActivity() != null) {
            recalculateAndBroadcastLeaderboard(question.getLiveActivity().getActivityId());
        }
    }

    // Give Score to User for Translation Game
    @Transactional
    public void awardScoreToUserForTranslationGame(int questionId, int userId, List<Integer> choiceIds, double timeTakenSeconds) {
        QuestionEntity question = questionRepo.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        userScoreRepo.findByUser_UserIdAndQuestion_QuestionId(userId, questionId)
                .ifPresent(existing -> {
                    throw new IllegalStateException("User already has a score for this question. Score: " + existing.getScore());
                });
                    
        boolean isCorrectChoice = choiceServ.validateTranslationGame(questionId, choiceIds);
        
        if (!isCorrectChoice) {
            throw new IllegalArgumentException("The selected choices are not in the correct order.");
        }

        int baseScore = question.getScore().getScore();
        int timeBonus = calculateTimeBonus(timeTakenSeconds);
        
        int awardedScore = baseScore + timeBonus;

        UserScore userScore = new UserScore(user, question, awardedScore);
        userScoreRepo.save(userScore);
        
        // Broadcast to Leaderboard
        if (question.getLiveActivity() != null) {
            recalculateAndBroadcastLeaderboard(question.getLiveActivity().getActivityId());
        }
    }

    // Total Score for User (for Live Activities)
    @SuppressWarnings("unused")
    public int getTotalScoreForLiveUser(int userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return userScoreRepo.findByUser_UserId(userId).stream()
                .filter(userScore -> {
                    QuestionEntity question = userScore.getQuestion();
                    // Only include scores from questions that belong to LessonActivities
                    // Edited this part to remove unnecessary lesson activity check
                    return question != null && question.getLiveActivity() != null;
                })
                .mapToInt(UserScore::getScore)
                .sum();
    }

    // Total Score for Live Activity
    public int getTotalScoreForLiveActivity(int liveActivityId) {
        // Find all questions for the live activity
        List<QuestionEntity> questions = questionRepo.findByLiveActivity_ActivityId(liveActivityId);

        // Sum up all scores from the questions
        return questions.stream()
                .filter(question -> question.getScore() != null)
                .mapToInt(question -> question.getScore().getScore())
                .sum();
    }

    // Leaderboard
    public List<LeaderboardEntry> getLiveActivityLeaderboard(int activityId) {
        return userScoreRepo.findLeaderboardByLiveActivity(activityId);
    }

    // Recalculate and broadcast leaderboard
    public void recalculateAndBroadcastLeaderboard(int activityId) {
        List<LeaderboardEntry> leaderboard = getLiveActivityLeaderboard(activityId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("leaderboard", leaderboard);
        
        liveActivityBroadcaster.broadcastUpdate(
            activityId,
            new LiveActivityUpdate(activityId, "LEADERBOARD_UPDATE", payload)
        );
    }

    @Transactional
    public void clearScoresForLiveActivity(int liveActivityId) {
        // Find all UserScore entries where the associated Question belongs to the given
        // LiveActivity
        List<UserScore> userScoresToClear = userScoreRepo.findByQuestion_LiveActivity_ActivityId(liveActivityId);

        if (!userScoresToClear.isEmpty()) {
            userScoreRepo.deleteAll(userScoresToClear);
            System.out.println(
                    "Cleared " + userScoresToClear.size() + " user scores for Live Activity ID: " + liveActivityId);
        } else {
            System.out.println("No user scores found to clear for Live Activity ID: " + liveActivityId);
        }
    }

    // Live Activity Score Statistics
    public Map<String, Double> getActivityScoreStatistics(int activityId) {
        Map<String, Double> statistics = new HashMap<>();

        List<Integer> scores = userScoreRepo.findScoresByActivityId(activityId);

        if (scores.isEmpty()) {
            statistics.put("average", 0.0);
            statistics.put("highest", 0.0);
            statistics.put("lowest", 0.0);
            return statistics;
        }

        DoubleSummaryStatistics stats = scores.stream()
                .mapToDouble(Integer::doubleValue)
                .summaryStatistics();

        statistics.put("average", stats.getAverage());
        statistics.put("highest", stats.getMax());
        statistics.put("lowest", stats.getMin());

        return statistics;
    }

}
