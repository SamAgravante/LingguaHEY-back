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
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
        summary = "Join a live activity lobby",
        description = "Allows a user to join the lobby for a specific live activity. Creates an entry if none exists.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully joined/rejoined lobby"),
            @ApiResponse(responseCode = "409", description = "User already in lobby",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Live Activity or User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> joinLobby(@PathVariable int activityId, @RequestParam int userId) {
        return userActivityLiveService.joinLobby(activityId, userId);
    }

    @GetMapping("/{activityId}/users")
    @Operation(
        summary = "Get users in a live activity lobby",
        description = "Retrieves a list of all users currently in the lobby for a specific live activity.",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Live Activity not found", // Though service might not throw this directly
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<UserEntity> getLobbyUsers(@PathVariable int activityId) {
        return userActivityLiveService.getLobbyUsers(activityId);
    }

    @PostMapping("/{activityId}/start")
    @Operation(
        summary = "Start a live activity",
        description = "Starts a live activity, deploys it for the classroom, and clears the lobby. Only a teacher can perform this action.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity started successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Only teacher can start activity",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Live Activity or Teacher not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict: Activity is already deployed/started",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> startActivity(@PathVariable int activityId, @RequestParam int teacherId) {
        return userActivityLiveService.startActivity(activityId, teacherId);
    }

    @DeleteMapping("/{activityId}/leave")
    @Operation(
        summary = "Leave a live activity lobby",
        description = "Allows a user to leave the lobby for a specific live activity.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully left lobby"),
            @ApiResponse(responseCode = "404", description = "User not found in lobby or already left",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> leaveLobby(@PathVariable int activityId, @RequestParam int userId) {
        return userActivityLiveService.leaveLobby(activityId, userId);
    }

    @PostMapping("/{activityId}/stop")
    @Operation(
        summary = "Stop a live activity and clear its temporary scores",
        description = "Stops a currently deployed live activity and permanently deletes all associated temporary user scores (leaderboard data). Also clears the live session entries for this activity. Only a teacher can perform this action.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity stopped and scores cleared successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Only teacher can stop activity",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Live Activity or Teacher not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict: Activity is not currently deployed/running",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> stopActivity(@PathVariable int activityId, @RequestParam int teacherId) {
        return userActivityLiveService.stopActivity(activityId, teacherId);
    }

    @PostMapping("/{activityId}/next-question")
    @Operation(
        summary = "Advance to the next question in a live activity",
        description = "Broadcasts the next question index and the current leaderboard to participants in the live activity.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Next question broadcasted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> nextQuestion(@PathVariable int activityId, @RequestParam int questionIndex, @RequestParam int teacherId) {
        // Fetch all users and their scores for this activity
        List<UserActivityLive> userActivities = userActivityLiveService.getUserActivities(activityId);
        List<LeaderboardEntry> leaderboard = userActivities.stream()
            .collect(Collectors.toMap(
                ua -> ua.getUser().getUserId(),
                ua -> new LeaderboardEntry(
                    ua.getUser().getUserId(),
                    ua.getUser().getFirstName() + " " + ua.getUser().getLastName(),
                    ua.getScore(),
                    ua.getUser().getProfilePic()
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
    @Operation(
        summary = "Finish a live activity quiz",
        description = "Broadcasts a 'FINISH_QUIZ' event to all participants, signaling the end of the quiz session.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Quiz finished event broadcasted"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> finishQuiz(@PathVariable int activityId, @RequestParam int teacherId) {
        // Broadcast FINISH_QUIZ event
        LiveActivityUpdate update = new LiveActivityUpdate(activityId, "FINISH_QUIZ", null);
        liveActivityBroadcaster.broadcastUpdate(activityId, update);

        return ResponseEntity.ok("Quiz finished");
    }
}