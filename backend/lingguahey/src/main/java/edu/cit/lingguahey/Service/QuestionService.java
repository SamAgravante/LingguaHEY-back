package edu.cit.lingguahey.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.lingguahey.Entity.ChoiceEntity;
//import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Repository.ChoiceRepository;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import edu.cit.lingguahey.Repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private LiveActivityRepository liveactivityRepo;
    

    @Autowired
    private ChoiceRepository choiceRepo;

    // Read All Questions
    public List<QuestionEntity> getAllQuestionEntity() {
        return questionRepo.findAll();
    }

    // Read Single Question
    public QuestionEntity getQuestionEntity(int questionId) {
        return questionRepo.findById(questionId).get();
    }

    // Update
    public QuestionEntity putQuestionEntity(int questionId, String questionDescription, String questionText, MultipartFile image) {
        try {
            QuestionEntity question = questionRepo.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question " + questionId + " not found!"));

            question.setQuestionDescription(questionDescription);
            question.setQuestionText(questionText);

            if (image != null && !image.isEmpty()) {
                question.setQuestionImage(image.getBytes()); // Store image as byte[]
            }

            return questionRepo.save(question);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image bytes", e);
        }
    }

    // Delete a QuestionEntity by id
    public String deleteQuestionEntity(int questionId) {
        if (questionRepo.existsById(questionId)) {
            List<ChoiceEntity> choices = choiceRepo.findByQuestion_QuestionId(questionId);
            choiceRepo.deleteAll(choices);

            questionRepo.deleteById(questionId);
            return "Question " + questionId + " and its associations deleted successfully!";
        } else {
            throw new EntityNotFoundException("Question " + questionId + " not found!");
        }
    }




    // Live Activity Handling
    // Create and Add Question to Live Activity
    public QuestionEntity postQuestionForLiveActivity(int liveActivityId, String questionDescription, String questionText, QuestionEntity.GameType gameType, MultipartFile image) {
        var liveActivity = liveactivityRepo.findById(liveActivityId)
            .orElseThrow(() -> new EntityNotFoundException("Live Activity not found with ID: " + liveActivityId));
        QuestionEntity question = new QuestionEntity();
        question.setLiveActivity(liveActivity);
        question.setQuestionDescription(questionDescription);
        question.setQuestionText(questionText);
        question.setGameType(gameType);
        if (image != null && !image.isEmpty()) {
            try {
                question.setQuestionImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return questionRepo.save(question);
    }

    // Read all questions for Live activity
    public List<QuestionEntity> getQuestionsForLiveActivity(int liveActivityId) {
        var liveActivity = liveactivityRepo.findById(liveActivityId)
            .orElseThrow(() -> new EntityNotFoundException("Live Activity not found with ID: " + liveActivityId));
        return liveActivity.getQuestions();
    }


    // Temporary Update for Live Activity
    public QuestionEntity putQuestionEntityLive(int questionId, String questionDescription, String questionText, QuestionEntity.GameType gameType, MultipartFile image) {
        try {
            QuestionEntity question = questionRepo.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question " + questionId + " not found!"));
    
            question.setQuestionDescription(questionDescription);
            question.setQuestionText(questionText);
            question.setGameType(gameType);
    
            if (image != null && !image.isEmpty()) {
                question.setQuestionImage(image.getBytes());
            }
    
            return questionRepo.save(question);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image bytes", e);
        }
    }
}

