package edu.cit.alibata.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.ActivityEntity;
import edu.cit.alibata.Repository.ActivityRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ActivityService {
    
    @Autowired
    ActivityRepository activityRepo;

    // Create
    public ActivityEntity postActivityEntity(ActivityEntity activity) {
        return activityRepo.save(activity);
    }

    // Read All Activities
    public List<ActivityEntity> getAllActivityEntity() {
        return activityRepo.findAll();
    }

    // Read Single Activity
    public ActivityEntity getActivityEntity(int activityId) {
        return activityRepo.findById(activityId).get();
    }

    // Update
    public ActivityEntity putActivityEntity(int activityId, ActivityEntity newActivity) {
        try {
            ActivityEntity activity = activityRepo.findById(activityId).get();
            activity.setActivityName(newActivity.getActivityName());
            activity.setCompleted(newActivity.isCompleted());
            activity.setGameType(newActivity.getGameType());
            return activityRepo.save(activity);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + activityId + " not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteActivityEntity(int activityId) {
        if (activityRepo.findById(activityId) != null) {
            activityRepo.deleteById(activityId);
            return "Activity " + activityId + " deleted successfully!";
        } else {
            return "Activity " + activityId + " not found!";
        }
    }
}

