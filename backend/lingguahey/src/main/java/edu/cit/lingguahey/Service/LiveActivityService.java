package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.ChoiceEntity;
import edu.cit.lingguahey.Entity.ClassroomActivityLive;
import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Entity.ClassroomUser;
import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.ChoiceRepository;
import edu.cit.lingguahey.Repository.ClassroomActivityLiveRepository;
import edu.cit.lingguahey.Repository.ClassroomRepository;
import edu.cit.lingguahey.Repository.ClassroomUserRepository;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import edu.cit.lingguahey.Repository.QuestionRepository;
import edu.cit.lingguahey.Repository.UserActivityLiveRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.model.ClassroomActivityLiveProjection;
import edu.cit.lingguahey.model.UserActivityLiveProjection;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LiveActivityService {
    
    @Autowired
    LiveActivityRepository activityRepo;

    @Autowired
    private UserActivityLiveRepository userActivityRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ClassroomRepository classroomRepo;

    @Autowired
    private ClassroomUserRepository classroomUserRepo;

    @Autowired
    private ClassroomActivityLiveRepository classroomActivityLiveRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ChoiceRepository choiceRepo;

    // Create and assign to classroom and users
    public LiveActivityEntity postActivityEntity(LiveActivityEntity activity, int classroomId) {
        ClassroomEntity classroom = classroomRepo.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found with ID: " + classroomId));
        activity.setClassroom(classroom);
        LiveActivityEntity postActivity = activityRepo.save(activity);
        
        ClassroomActivityLive classroomActivity = new ClassroomActivityLive(classroom, activity);
        classroomActivityLiveRepo.save(classroomActivity);

        List<ClassroomUser> classroomUsers = classroomUserRepo.findByClassroom_ClassroomID(classroomId);
        for (ClassroomUser classroomUser : classroomUsers) {
            UserEntity user = classroomUser.getUser();
            UserActivityLive userActivity = new UserActivityLive(user, postActivity);
            userActivityRepo.save(userActivity);
        }

        return postActivity;
    }

    // Read All Activities
    public List<LiveActivityEntity> getAllActivityEntity() {
        return activityRepo.findAll();
    }

    // Read Single Activity
    public LiveActivityEntity getActivityEntity(int activityId) {
        return activityRepo.findById(activityId).get();
    }

    // Read all activities for user
    @SuppressWarnings("unused")
    public List<UserActivityLiveProjection> getAllActivitiesForUser(int userId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return userActivityRepo.findByUser_UserId(userId);
    }

    // Read live activity for a classroom
    public List<ClassroomActivityLiveProjection> getAllActivitiesForClassroom(int classroomId) {
        classroomRepo.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found with ID: " + classroomId));

        return classroomActivityLiveRepo.findByClassroom_ClassroomID(classroomId);
    }

    // Update
    public LiveActivityEntity putActivityEntity(int activityId, LiveActivityEntity newActivity) {
        try {
            LiveActivityEntity activity = activityRepo.findById(activityId).get();
            activity.setActivityName(newActivity.getActivityName());
            activity.setDeployed(newActivity.isDeployed());
            activity.setGameType(newActivity.getGameType());
            if (newActivity.getQuestions() != null) {
                activity.setQuestions(newActivity.getQuestions());
            }
            return activityRepo.save(activity);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + activityId + " not found!");
        }
    }

    // Delete an ActivityEntity by id
    public String deleteActivityEntity(int activityId) {
        if (activityRepo.existsById(activityId)) {
            List<UserActivityLive> userActivities = userActivityRepo.findByActivity_ActivityId(activityId);
            userActivityRepo.deleteAll(userActivities);
            List<ClassroomActivityLive> classroomActivities = classroomActivityLiveRepo.findByActivity_ActivityId(activityId);
            classroomActivityLiveRepo.deleteAll(classroomActivities);

            List<QuestionEntity> questions = questionRepo.findByActivity_ActivityId(activityId);
            for (QuestionEntity question : questions) {
                List<ChoiceEntity> choices = choiceRepo.findByQuestion_QuestionId(question.getQuestionId());
                choiceRepo.deleteAll(choices);
                questionRepo.delete(question);
            }

            activityRepo.deleteById(activityId);
            return "Activity " + activityId + " and its associations deleted successfully!";
        } else {
            throw new EntityNotFoundException("Activity " + activityId + " not found!");
        }
    }
}

