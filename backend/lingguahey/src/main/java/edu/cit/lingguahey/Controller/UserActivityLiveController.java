package edu.cit.lingguahey.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Entity.Role;
import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.LiveActivityRepository;
import edu.cit.lingguahey.Repository.UserActivityLiveRepository;
import edu.cit.lingguahey.Repository.UserRepository;

// Example: LobbyController.java
@RestController
@RequestMapping("/api/lobby")
public class UserActivityLiveController {

    @Autowired
    private UserActivityLiveRepository userActivityLiveRepository;

    @Autowired
    private LiveActivityRepository liveActivityRepository;

    @Autowired
    private UserRepository userRepository;

    // User joins lobby
    @PostMapping("/{activityId}/join")
    public ResponseEntity<?> joinLobby(@PathVariable int activityId, @RequestParam int userId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        UserActivityLive userActivity = new UserActivityLive(user, activity);
        userActivityLiveRepository.save(userActivity);
        return ResponseEntity.ok("Joined lobby");
    }

    // Get lobby users
    @GetMapping("/{activityId}/users")
    public List<UserEntity> getLobbyUsers(@PathVariable int activityId) {
        List<UserActivityLive> lobbyEntries = userActivityLiveRepository.findByActivity_ActivityIdAndInLobby(activityId, true);
        List<UserEntity> users = new ArrayList<>();
        for (UserActivityLive entry : lobbyEntries) {
            users.add(entry.getUser());
        }
        return users;
    }

    // Teacher starts activity
    @PostMapping("/{activityId}/start")
    public ResponseEntity<?> startActivity(@PathVariable int activityId, @RequestParam int teacherId) {
        LiveActivityEntity activity = liveActivityRepository.findById(activityId).orElseThrow();
        UserEntity teacher = userRepository.findById(teacherId).orElseThrow();
        if (teacher.getRole() != Role.TEACHER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teacher can start activity");
        }
        // Move all lobby users to active (set inLobby to false)
        List<UserActivityLive> lobbyEntries = userActivityLiveRepository.findByActivity_ActivityIdAndInLobby(activityId, true);
        for (UserActivityLive entry : lobbyEntries) {
            entry.setInLobby(false);
        }
        userActivityLiveRepository.saveAll(lobbyEntries);
        activity.setDeployed(true);
        liveActivityRepository.save(activity);
        return ResponseEntity.ok("Activity started");
    }
}