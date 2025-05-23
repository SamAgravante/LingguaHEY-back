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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public ResponseEntity<?> joinLobby(int activityId, int userId) {
        // Check if user is already in the lobby
        boolean alreadyInLobby = userActivityLiveRepository
            .findByActivity_ActivityIdAndInLobby(activityId, true)
            .stream()
            .anyMatch(entry -> entry.getUser().getUserId() == userId);
    
        if (alreadyInLobby) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already in lobby");
        }
    
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

    public ResponseEntity<?> startActivity(int activityId, int teacherId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity teacher = userRepository.findById(teacherId).orElseThrow();
        if (teacher.getRole() != Role.TEACHER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teacher can start activity");
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