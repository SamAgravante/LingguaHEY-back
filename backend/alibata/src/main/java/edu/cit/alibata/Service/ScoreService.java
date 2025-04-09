package edu.cit.alibata.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.ScoreEntity;
import edu.cit.alibata.Repository.ScoreRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepo;

    // Create
    public ScoreEntity postScoreEntity(ScoreEntity score) {
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
        return scoreRepo.save(score);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + scoreId + " not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteScoreEntity(int scoreId) {
        if (scoreRepo.findById(scoreId) != null) {
            scoreRepo.deleteById(scoreId);
            return "Score " + scoreId + " deleted successfully!";
        } else {
            return "Score " + scoreId + " not found!";
        }
    }
}

