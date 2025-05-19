package edu.cit.lingguahey.Service;

import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Entity.Role;
import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import edu.cit.lingguahey.Repository.UserActivityLiveRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserActivityLiveService {

    @Autowired
    private UserActivityLiveRepository userActivityLiveRepository;

    @Autowired
    private LiveActivityRepository liveActivityRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> joinLobby(int activityId, int userId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        UserActivityLive userActivity = new UserActivityLive(user, activity);
        userActivityLiveRepository.save(userActivity);
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

    public ResponseEntity<?> startActivity(int activityId, int teacherId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity teacher = userRepository.findById(teacherId).orElseThrow();
        if (teacher.getRole() != Role.TEACHER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teacher can start activity");
        }
        List<UserActivityLive> lobbyEntries = userActivityLiveRepository.findByActivity_ActivityIdAndInLobby(activityId, true);
        for (UserActivityLive entry : lobbyEntries) {
            entry.setInLobby(false);
        }
        userActivityLiveRepository.saveAll(lobbyEntries);
        activity.setDeployed(true);
        liveActivityRepository.save(activity);
        return ResponseEntity.ok("Activity started");
    }

    public ResponseEntity<?> leaveLobby(int activityId, int userId) {
        List<UserActivityLive> userActivities = userActivityLiveRepository
        .findByActivity_ActivityIdAndInLobby(activityId, true)
        .stream()
        .filter(entry -> entry.getUser().getUserId() == userId)
        .toList();

        if (userActivities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in lobby");
        }
        userActivityLiveRepository.deleteAll(userActivities);
        return ResponseEntity.ok("Left lobby");
    }
}