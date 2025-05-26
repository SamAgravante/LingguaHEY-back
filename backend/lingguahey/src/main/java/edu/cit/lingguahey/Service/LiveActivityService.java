package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Broadcaster.LiveActivityBroadcaster;
import edu.cit.lingguahey.DTO.LiveActivityUpdate;
import edu.cit.lingguahey.Entity.ChoiceEntity;
import edu.cit.lingguahey.Entity.ClassroomActivityLive;
import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Entity.ClassroomUser;
import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Entity.UserScore;
import edu.cit.lingguahey.Repository.ChoiceRepository;
import edu.cit.lingguahey.Repository.ClassroomActivityLiveRepository;
import edu.cit.lingguahey.Repository.ClassroomRepository;
import edu.cit.lingguahey.Repository.ClassroomUserRepository;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import edu.cit.lingguahey.Repository.QuestionRepository;
import edu.cit.lingguahey.Repository.UserActivityLiveRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.Repository.UserScoreRepository;
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

    @Autowired
    private LiveActivityBroadcaster broadcaster;

    @Autowired
    private UserScoreRepository userScoreRepo;

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

        // Broadcast creation event
        broadcaster.broadcastUpdate(
            postActivity.getActivityId(),
            new LiveActivityUpdate(postActivity.getActivityId(), "CREATED", postActivity)
        );

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

    // Read deployed activity for a classroom
    public int getDeployedActivityForClassroom(int classroomId) {
        classroomRepo.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found with ID: " + classroomId));

        Optional<LiveActivityEntity> deployedActivity = activityRepo.findByActivityClassroom_ClassroomIDAndIsDeployedTrue(classroomId);

        return deployedActivity.orElseThrow(() -> new EntityNotFoundException("No deployed live activity found for classroom ID: " + classroomId))
            .getActivityId();
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
            LiveActivityEntity updated = activityRepo.save(activity);

            // Broadcast update event
            broadcaster.broadcastUpdate(
                updated.getActivityId(),
                new LiveActivityUpdate(updated.getActivityId(), "UPDATED", updated)
            );

            return updated;
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

            List<QuestionEntity> questions = questionRepo.findByLiveActivity_ActivityId(activityId);
            for (QuestionEntity question : questions) {
                List<ChoiceEntity> choices = choiceRepo.findByQuestion_QuestionId(question.getQuestionId());
                choiceRepo.deleteAll(choices);
                questionRepo.delete(question);
            }

            activityRepo.deleteById(activityId);

            // Broadcast delete event
            broadcaster.broadcastUpdate(
                activityId,
                new LiveActivityUpdate(activityId, "DELETED", null)
            );

            return "Activity " + activityId + " and its associations deleted successfully!";
        } else {
            throw new EntityNotFoundException("Activity " + activityId + " not found!");
        }
    }

    // Update activity deployed status
    @Transactional
    public LiveActivityEntity setActivityDeployedStatus(int activityId, boolean deploy) {
        LiveActivityEntity activityToDeploy = activityRepo.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException("Activity " + activityId + " not found!"));

        int classroomId = activityToDeploy.getClassroom().getClassroomID();

        Optional<ClassroomActivityLive> existingDeployedJunction = classroomActivityLiveRepo
            .findByClassroom_ClassroomIDAndDeployedTrue(classroomId);

        if (existingDeployedJunction.isPresent()) {
            ClassroomActivityLive oldDeployedCAL = existingDeployedJunction.get();
            if (oldDeployedCAL.getActivity().getActivityId() != activityId) {
                oldDeployedCAL.setDeployed(false);
                classroomActivityLiveRepo.save(oldDeployedCAL);

                LiveActivityEntity oldDeployedActivity = oldDeployedCAL.getActivity();
                oldDeployedActivity.setDeployed(false);
                activityRepo.save(oldDeployedActivity);
                deleteUserScoresForLiveActivity(oldDeployedActivity.getActivityId());
            }
        }

        activityToDeploy.setDeployed(deploy);
        LiveActivityEntity updatedActivity = activityRepo.save(activityToDeploy);

        ClassroomActivityLive targetCAL = classroomActivityLiveRepo
            .findByClassroom_ClassroomIDAndActivity_ActivityId(classroomId, activityId)
            .orElseGet(() -> {
                ClassroomActivityLive newCal = new ClassroomActivityLive(activityToDeploy.getClassroom(), activityToDeploy);
                return classroomActivityLiveRepo.save(newCal);
            });
        targetCAL.setDeployed(deploy);
        classroomActivityLiveRepo.save(targetCAL);

        if (deploy) {
            deleteUserScoresForLiveActivity(activityId);
        } else {
            deleteUserScoresForLiveActivity(activityId);
        }

        broadcaster.broadcastUpdate(
            updatedActivity.getActivityId(),
            new LiveActivityUpdate(updatedActivity.getActivityId(), "DEPLOYMENT_STATUS_UPDATED", updatedActivity)
        );

        return updatedActivity;
    }

    // delete all UserScore entries associated with questions
    @Transactional
    private void deleteUserScoresForLiveActivity(int liveActivityId) {
        List<QuestionEntity> questions = questionRepo.findByLiveActivity_ActivityId(liveActivityId);
        for (QuestionEntity question : questions) {
            deleteUserScoresForQuestion(question.getQuestionId());
        }
    }

    // delete all UserScore entries for a specific question
    @Transactional
    private void deleteUserScoresForQuestion(int questionId) {
        List<UserScore> userScores = userScoreRepo.findByQuestion_QuestionId(questionId);
        if (!userScores.isEmpty()) {
            userScoreRepo.deleteAll(userScores);
        }
    }
}