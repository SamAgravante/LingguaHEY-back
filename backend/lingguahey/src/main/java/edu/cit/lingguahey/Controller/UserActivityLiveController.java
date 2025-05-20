package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Service.UserActivityLiveService;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/lingguahey/lobby")
@Tag(name = "Lobby")
public class UserActivityLiveController {

    @Autowired
    private UserActivityLiveService userActivityLiveService;

    @PostMapping("/{activityId}/join")
    public ResponseEntity<?> joinLobby(@PathVariable int activityId, @RequestParam int userId) {
        return userActivityLiveService.joinLobby(activityId, userId);
    }

    @GetMapping("/{activityId}/users")
    public List<UserEntity> getLobbyUsers(@PathVariable int activityId) {
        return userActivityLiveService.getLobbyUsers(activityId);
    }

    @PostMapping("/{activityId}/start")
    public ResponseEntity<?> startActivity(@PathVariable int activityId, @RequestParam int teacherId) {
        return userActivityLiveService.startActivity(activityId, teacherId);
    }

    @DeleteMapping("/{activityId}/leave")
    public ResponseEntity<?> leaveLobby(@PathVariable int activityId, @RequestParam int userId) {
        return userActivityLiveService.leaveLobby(activityId, userId);
    }
}