package edu.cit.lingguahey.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Broadcaster.LiveActivityBroadcaster;
import edu.cit.lingguahey.DTO.LeaderboardEntry;
import edu.cit.lingguahey.DTO.LiveActivityUpdate;
import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Service.UserActivityLiveService;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/lingguahey/lobby")
@Tag(name = "Lobby")
public class UserActivityLiveController {

    @Autowired
    private UserActivityLiveService userActivityLiveService;

    @Autowired
    private LiveActivityBroadcaster liveActivityBroadcaster;

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

    @PostMapping("/{activityId}/next-question")
    public ResponseEntity<?> nextQuestion(
            @PathVariable int activityId,
            @RequestParam int questionIndex,
            @RequestParam int teacherId) {

        // Fetch all users and their scores for this activity
        List<UserActivityLive> userActivities = userActivityLiveService.getUserActivities(activityId);
        List<LeaderboardEntry> leaderboard = userActivities.stream()
            .collect(Collectors.toMap(
                ua -> ua.getUser().getUserId(),
                ua -> new LeaderboardEntry(
                    ua.getUser().getUserId(),
                    ua.getUser().getFirstName() + " " + ua.getUser().getLastName(),
                    ua.getScore()
                ),
                (a, b) -> a // keep first if duplicate
            ))
            .values()
            .stream()
            .collect(Collectors.toList());

        // Send both question index and leaderboard
        Map<String, Object> payload = new HashMap<>();
        payload.put("questionIndex", questionIndex);
        payload.put("leaderboard", leaderboard);

        LiveActivityUpdate update = new LiveActivityUpdate(activityId, "NEXT_QUESTION", payload);
        liveActivityBroadcaster.broadcastUpdate(activityId, update);
        return ResponseEntity.ok("Next question broadcasted");
    }

    @PostMapping("/{activityId}/finish-quiz")
    public ResponseEntity<?> finishQuiz(
            @PathVariable int activityId,
            @RequestParam int teacherId) {

        // Optionally: validate teacherId is the teacher for this activity

        // Broadcast FINISH_QUIZ event
        LiveActivityUpdate update = new LiveActivityUpdate(activityId, "FINISH_QUIZ", null);
        liveActivityBroadcaster.broadcastUpdate(activityId, update);

        return ResponseEntity.ok("Quiz finished");
    }
}