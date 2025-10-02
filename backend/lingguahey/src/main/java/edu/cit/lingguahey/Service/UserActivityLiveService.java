package edu.cit.lingguahey.Service;

import edu.cit.lingguahey.Broadcaster.LobbyBroadcaster;
import edu.cit.lingguahey.DTO.LobbyUpdate;
import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Entity.Role;
import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import edu.cit.lingguahey.Repository.UserActivityLiveRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.model.LobbyDTO;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserActivityLiveService {

    @Autowired
    private UserActivityLiveRepository userActivityLiveRepository;

    @Autowired
    private LiveActivityRepository liveActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyBroadcaster lobbyBroadcaster;

    @Autowired
    private LiveActivityService liveActivityService;

    @Autowired
    private ScoreService scoreService;

    public ResponseEntity<?> joinLobby(int activityId, int userId) {
        // Use the Optional-based repository method
        Optional<UserActivityLive> existingEntryOpt = userActivityLiveRepository
            .findByUser_UserIdAndActivity_ActivityId(userId, activityId);
    
        if (existingEntryOpt.isPresent()) {
            UserActivityLive entry = existingEntryOpt.get();
            if (entry.isInLobby()) {
                return ResponseEntity.ok("User already confirmed in lobby");
            } else {
                entry.setInLobby(true);
                userActivityLiveRepository.save(entry);
                broadcastLobby(activityId);
                return ResponseEntity.ok("Rejoined lobby");
            }
        }
    
        // No record exists, create new
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        UserActivityLive userActivity = new UserActivityLive(user, activity);
        userActivityLiveRepository.save(userActivity);
        broadcastLobby(activityId);
        return ResponseEntity.ok("Joined lobby");
    }

    public List<UserEntity> getLobbyUsers(int activityId) {
        List<UserActivityLive> lobbyEntries = userActivityLiveRepository.findByActivity_ActivityIdAndInLobby(activityId, true);
        List<UserEntity> users = new ArrayList<>();
        for (UserActivityLive entry : lobbyEntries) {
            users.add(entry.getUser());
        }
        return users;
    }

    @Transactional
    public ResponseEntity<?> startActivity(int activityId, int teacherId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity teacher = userRepository.findById(teacherId).orElseThrow();
        if (teacher.getRole() != Role.TEACHER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teacher can start activity");
        }

        if (activity.isDeployed()) {
            System.out.println("Live activity ID " + activityId + " is already deployed. Clearing previous scores for rerun.");
            scoreService.clearScoresForLiveActivity(activityId);
        }

        List<UserActivityLive> lobbyEntries = userActivityLiveRepository
            .findByActivity_ActivityIdAndInLobby(activityId, true);

        // Mark everyone as left the lobby
        lobbyEntries.forEach(entry -> entry.setInLobby(false));
        userActivityLiveRepository.saveAll(lobbyEntries);

        activity.setDeployed(true);
        liveActivityRepository.save(activity);

        // 1) Send an UPDATE so clients see an empty lobby if you want
        broadcastLobby(activityId);

        // 2) Send a START event so frontend can navigate
        lobbyBroadcaster.broadcastStartMessage(activityId);

        return ResponseEntity.ok("Activity started");
    }

    @Transactional
    public ResponseEntity<?> stopActivity(int activityId, int teacherId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException("Live Activity not found with ID: " + activityId));
        UserEntity teacher = userRepository.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + teacherId));

        if (teacher.getRole() != Role.TEACHER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teacher can stop activity");
        }

        if (!activity.isDeployed()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Activity is not currently deployed/running.");
        }

        scoreService.clearScoresForLiveActivity(activityId);
        liveActivityService.setActivityDeployedStatus(activityId, false);
        List<UserActivityLive> activeEntries = userActivityLiveRepository.findByActivity_ActivityId(activityId);
        userActivityLiveRepository.deleteAll(activeEntries);

        lobbyBroadcaster.broadcastStopMessage(activityId);

        return ResponseEntity.ok("Activity stopped and scores cleared.");
    }

    public ResponseEntity<?> leaveLobby(int activityId, int userId) {
        List<UserActivityLive> userActivities = userActivityLiveRepository
            .findByActivity_ActivityIdAndInLobby(activityId, true)
            .stream()
            .filter(entry -> entry.getUser().getUserId() == userId)
            .toList();

        if (userActivities.isEmpty()) {
            return ResponseEntity.ok("User already left or was not found in the lobby.");
        }
        userActivityLiveRepository.deleteAll(userActivities);
        broadcastLobby(activityId);
        return ResponseEntity.ok("Left lobby");
    }

    private void broadcastLobby(int activityId) {
        List<LobbyDTO> users = userActivityLiveRepository
            .findByActivity_ActivityIdAndInLobby(activityId, true)
            .stream()
            .map(entry -> {
                var u = entry.getUser();
                return new LobbyDTO(u.getUserId(), u.getFirstName(), u.getLastName(), u.getRole().name(), u.getProfilePic());
            })
            .collect(Collectors.toList());

        LobbyUpdate update = new LobbyUpdate(users);
        lobbyBroadcaster.broadcastLobbyUpdate(activityId, update);
    }

    public List<UserActivityLive> getUserActivities(int activityId) {
        return userActivityLiveRepository.findByActivity_ActivityId(activityId);    
    }
}