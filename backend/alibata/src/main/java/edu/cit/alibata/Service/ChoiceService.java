package edu.cit.alibata.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.ChoiceEntity;
import edu.cit.alibata.Repository.ChoiceRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepo;

    // Create
    public ChoiceEntity postChoiceEntity(ChoiceEntity choice) {
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

    // Update
    public ChoiceEntity putChoiceEntity(int choiceId, ChoiceEntity newChoice) {
        try {
        ChoiceEntity choice = choiceRepo.findById(choiceId).get();
        choice.setChoiceText(newChoice.getChoiceText());
        choice.setCorrect(newChoice.isCorrect());
        return choiceRepo.save(choice);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + choiceId + " not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteChoiceEntity(int choiceId) {
        if (choiceRepo.findById(choiceId) != null) {
            choiceRepo.deleteById(choiceId);
            return "Choice " + choiceId + " deleted successfully!";
        } else {
            return "Choice " + choiceId + " not found!";
        }
    }
}

