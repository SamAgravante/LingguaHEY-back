package edu.cit.lingguahey.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.ChoiceEntity;
import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Repository.ChoiceRepository;
import edu.cit.lingguahey.Repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepo;

    @Autowired
    private QuestionRepository questionRepo;

    // Create and Add Choice to Question
    public ChoiceEntity postChoiceForQuestion(int questionId, ChoiceEntity choice) {
        var question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        choice.setQuestion(question);
        return choiceRepo.save(choice);
    }

    // Read All Choices
    public List<ChoiceEntity> getAllChoiceEntity() {
        return choiceRepo.findAll();
    }

    // Read Single Choice
    public ChoiceEntity getChoiceEntity(int choiceId) {
        return choiceRepo.findById(choiceId).get();
    }

    // Read all choices for question
    public List<ChoiceEntity> getChoicesForQuestion(int questionId) {
        var question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        return question.getChoices();
    }

    // Update
    public ChoiceEntity putChoiceEntity(int choiceId, ChoiceEntity newChoice) {
        try {
        ChoiceEntity choice = choiceRepo.findById(choiceId).get();
        choice.setChoiceText(newChoice.getChoiceText());
        choice.setCorrect(newChoice.isCorrect());
        if (newChoice.getQuestion() != null) {
            choice.setQuestion(newChoice.getQuestion());
        }
        return choiceRepo.save(choice);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Choice " + choiceId + " not found!");
        }
    }

    // Delete a ChoiceEntity by id
    public String deleteChoiceEntity(int choiceId) {
        if (choiceRepo.existsById(choiceId)) {
            choiceRepo.deleteById(choiceId);
            return "Choice " + choiceId + " deleted successfully!";
        } else {
            throw new EntityNotFoundException("Choice " + choiceId + " not found!");
        }
    }

    // Validate the choices for a question in the translation game
    public boolean validateTranslationGame(int questionId, List<Integer> choiceIds) {
        QuestionEntity question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
        List<ChoiceEntity> choices = question.getChoices();
        List<ChoiceEntity> orderedChoices = choices.stream()
            .filter(choice -> choice.getChoiceOrder() != null)
            .sorted(Comparator.comparing(ChoiceEntity::getChoiceOrder))
            .toList();
        if (choiceIds.size() != orderedChoices.size()) {
            return false;
        }
        for (int i = 0; i < choiceIds.size(); i++) {
            if (!choiceIds.get(i).equals(orderedChoices.get(i).getChoiceId())) {
                return false;
            }
        }
        return true;
    }
}

