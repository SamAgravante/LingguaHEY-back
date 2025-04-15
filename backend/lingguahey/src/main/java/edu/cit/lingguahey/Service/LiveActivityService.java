package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LiveActivityService {
    
    @Autowired
    LiveActivityRepository activityRepo;

    // Create
    public LiveActivityEntity postActivityEntity(LiveActivityEntity activity) {
        return activityRepo.save(activity);
    }

    // Read All Activities
    public List<LiveActivityEntity> getAllActivityEntity() {
        return activityRepo.findAll();
    }

    // Read Single Activity
    public LiveActivityEntity getActivityEntity(int activityId) {
        return activityRepo.findById(activityId).get();
    }

    // Update
    public LiveActivityEntity putActivityEntity(int activityId, LiveActivityEntity newActivity) {
        try {
            LiveActivityEntity activity = activityRepo.findById(activityId).get();
            activity.setActivityName(newActivity.getActivityName());
            activity.setDeployed(newActivity.isDeployed());
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

