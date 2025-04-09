package edu.cit.alibata.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.QuestionEntity;
import edu.cit.alibata.Repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepo;

    // Create
    public QuestionEntity postQuestionEntity(QuestionEntity question) {
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

    // Update
    public QuestionEntity putQuestionEntity(int questionId, QuestionEntity newQuestion) {
        try {
            QuestionEntity question = questionRepo.findById(questionId).get();
            question.setQuestionDescription(newQuestion.getQuestionDescription());
            question.setQuestionText(newQuestion.getQuestionText());
            question.setQuestionImage(newQuestion.getQuestionImage());
            return questionRepo.save(question);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + questionId + " not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteQuestionEntity(int questionId) {
        if (questionRepo.findById(questionId) != null) {
            questionRepo.deleteById(questionId);
            return "Question " + questionId + " deleted successfully!";
        } else {
            return "Question " + questionId + " not found!";
        }
    }
}

