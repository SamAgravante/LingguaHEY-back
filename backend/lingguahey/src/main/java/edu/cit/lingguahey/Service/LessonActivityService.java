package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.ChoiceEntity;
import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Entity.ClassroomUser;
import edu.cit.lingguahey.Entity.LessonActivityEntity;
import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Entity.UserActivity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.ChoiceRepository;
import edu.cit.lingguahey.Repository.ClassroomRepository;
import edu.cit.lingguahey.Repository.ClassroomUserRepository;
import edu.cit.lingguahey.Repository.LessonActivityRepository;
import edu.cit.lingguahey.Repository.QuestionRepository;
import edu.cit.lingguahey.Repository.UserActivityRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.model.UserActivityProjection;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LessonActivityService {
    
    @Autowired
    private LessonActivityRepository activityRepo;

    @Autowired
    private UserActivityRepository userActivityRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ClassroomRepository classroomRepo;

    @Autowired
    private ClassroomUserRepository classroomUserRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ChoiceRepository choiceRepo;

    // Create and assign to classroom and users
    public LessonActivityEntity postActivityEntity(LessonActivityEntity activity, int classroomId) {
        ClassroomEntity classroom = classroomRepo.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found with ID: " + classroomId));
        activity.setLessonClassroom(classroom);
        LessonActivityEntity postActivity = activityRepo.save(activity);

        List<ClassroomUser> classroomUsers = classroomUserRepo.findByClassroom_ClassroomID(classroomId);
        for (ClassroomUser classroomUser : classroomUsers) {
            UserEntity user = classroomUser.getUser();
            UserActivity userActivity = new UserActivity(user, postActivity);
            userActivityRepo.save(userActivity);
        }

        return postActivity;
    }

    // Read All Activities
    public List<LessonActivityEntity> getAllActivityEntity() {
        return activityRepo.findAll();
    }

    // Read Single Activity
    public LessonActivityEntity getActivityEntity(int activityId) {
        return activityRepo.findById(activityId).get();
    }

    // Read all activities for user
    @SuppressWarnings("unused")
    public List<UserActivityProjection> getAllActivitiesForUser(int userId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return userActivityRepo.findByUser_UserId(userId);
    }

    // Read all activities for a classroom
    public List<LessonActivityEntity> getAllActivitiesForClassroom(int classroomId) {
        classroomRepo.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found with ID: " + classroomId));

        return activityRepo.findByLessonClassroom_ClassroomID(classroomId);
    }

    // Update
    public LessonActivityEntity putActivityEntity(int activityId, LessonActivityEntity newActivity) {
        try {
            LessonActivityEntity activity = activityRepo.findById(activityId).get();
            activity.setLessonName(newActivity.getLessonName());
            activity.setCompleted(newActivity.isCompleted());
            activity.setGameType(newActivity.getGameType());
            if (newActivity.getQuestions() != null) {
                activity.setQuestions(newActivity.getQuestions());
            }
            if (newActivity.getUsers() != null) {
                activity.setUsers(newActivity.getUsers());
            }
            return activityRepo.save(activity);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + activityId + " not found!");
        }
    }

    // Delete an ActivityEntity by id
    public String deleteActivityEntity(int activityId) {
        if (activityRepo.existsById(activityId)) {
            List<UserActivity> userActivities = userActivityRepo.findByActivity_ActivityId(activityId);
            userActivityRepo.deleteAll(userActivities);

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

    // Mark Activity as Completed
    public void markActivityAsCompleted(int userId, int activityId) {
        UserActivity userActivity = userActivityRepo.findByUser_UserIdAndActivity_ActivityId(userId, activityId)
            .orElseThrow(() -> new EntityNotFoundException("Activity not assigned to user"));
        userActivity.setCompleted(true);
        userActivityRepo.save(userActivity);
    }
}

