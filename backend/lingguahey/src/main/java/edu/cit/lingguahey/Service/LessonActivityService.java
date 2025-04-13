package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.LessonActivityEntity;
import edu.cit.lingguahey.Repository.LessonActivityRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LessonActivityService {
    
    @Autowired
    LessonActivityRepository LessonRepo;

    // Create
    public LessonActivityEntity postActivityEntity(LessonActivityEntity lesson) {
        return LessonRepo.save(lesson);
    }

    // Read All Activities
    public List<LessonActivityEntity> getAllActivityEntity() {
        return LessonRepo.findAll();
    }

    // Read Single Activity
    public LessonActivityEntity getActivityEntity(int lessonId) {
        return LessonRepo.findById(lessonId).get();
    }

    // Update
    public LessonActivityEntity putActivityEntity(int activityId, LessonActivityEntity newActivity) {
        try {
            LessonActivityEntity activity = LessonRepo.findById(activityId).get();
            activity.setLessonName(newActivity.getLessonName());
            activity.setCategory(newActivity.getCategory());
            activity.setCompleted(newActivity.isCompleted());
            return LessonRepo.save(activity);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + activityId + " not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteActivityEntity(int activityId) {
        if (LessonRepo.findById(activityId) != null) {
            LessonRepo.deleteById(activityId);
            return "Activity " + activityId + " deleted successfully!";
        } else {
            return "Activity " + activityId + " not found!";
        }
    }
}

