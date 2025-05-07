package edu.cit.lingguahey.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Repository.LessonActivityRepository;
import edu.cit.lingguahey.Repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private LessonActivityRepository activityRepo;

    // Create
    /*public QuestionEntity postQuestionEntity(QuestionEntity question) {
        return questionRepo.save(question);
    }*/

    // Create and Add Question to Activity
    public QuestionEntity postQuestionForActivity(int activityId, String questionDescription, String questionText, MultipartFile image) {
        var activity = activityRepo.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException("Activity not found with ID: " + activityId));
        QuestionEntity question = new QuestionEntity();
        question.setActivity(activity);
        question.setQuestionDescription(questionDescription);
        question.setQuestionText(questionText);
        if (image != null && !image.isEmpty()) {
            try {
                question.setQuestionImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return questionRepo.save(question);
    }

    // Read All Questions
    public List<QuestionEntity> getAllQuestionEntity() {
        return questionRepo.findAll();
    }

    // Read Single Question
    public QuestionEntity getQuestionEntity(int questionId) {
        return questionRepo.findById(questionId).get();
    }

    // Read all questions for activity
    public List<QuestionEntity> getQuestionsForActivity(int activityId) {
        var activity = activityRepo.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException("Activity not found with ID: " + activityId));
        return activity.getQuestions();
    }

    // Update
    public QuestionEntity putQuestionEntity(int questionId, QuestionEntity newQuestion) {
        try {
            QuestionEntity question = questionRepo.findById(questionId).get();
            question.setQuestionDescription(newQuestion.getQuestionDescription());
            question.setQuestionText(newQuestion.getQuestionText());
            question.setQuestionImage(newQuestion.getQuestionImage());
            if (newQuestion.getActivity() != null) {
                question.setActivity(newQuestion.getActivity());
            }
            if (newQuestion.getChoices() != null) {
                question.setChoices(newQuestion.getChoices());
            }
            return questionRepo.save(question);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Question " + questionId + " not found!");
        }
    }

    // Delete
    public String deleteQuestionEntity(int questionId) {
        if (questionRepo.existsById(questionId)) {
            questionRepo.deleteById(questionId);
            return "Question " + questionId + " deleted successfully!";
        } else {
            return "Question " + questionId + " not found!";
        }
    }
}

