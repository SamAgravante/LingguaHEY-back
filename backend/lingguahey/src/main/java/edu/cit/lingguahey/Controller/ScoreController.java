package edu.cit.lingguahey.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.ScoreEntity;
import edu.cit.lingguahey.Service.ScoreService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.LeaderboardEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/lingguahey/scores")
@Tag(name = "Score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    // Create and Add Score to Question
    @PostMapping("/questions/{questionId}")
    @Operation(
        summary = "Set a score for a question",
        description = "Creates a score for a specific question",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Score value to set",
            content = @Content(schema = @Schema(implementation = Integer.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Score created successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('teacher:create')")
    public ResponseEntity<ScoreEntity> setScoreForQuestion(@PathVariable int questionId, @RequestParam int scoreValue) {
        ScoreEntity postScore = scoreService.setScoreForQuestion(questionId, scoreValue);
        return ResponseEntity.status(201).body(postScore);
    }

    // Read All Scores
    @GetMapping("")
    @Operation(
        summary = "Get all scores",
        description = "Retrieves a list of all scores",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<ScoreEntity>> getAllScoreEntity() {
        List<ScoreEntity> scores = scoreService.getAllScoreEntity();
        return ResponseEntity.ok().body(scores);
    }

    // Read Single Score
    @GetMapping("/{id}")
    @Operation(
        summary = "Get a score by ID",
        description = "Retrieves a specific score by its ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Score not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<ScoreEntity> getScoreEntity(@PathVariable int id) {
        ScoreEntity score = scoreService.getScoreEntity(id);
        return ResponseEntity.ok().body(score);
    }

    // Update Score for Question
    @PutMapping("/questions/{questionId}/score")
    @Operation(
        summary = "Update a score for a question",
        description = "Updates the score for a specific question by its ID",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Score value to set",
            content = @Content(schema = @Schema(implementation = Integer.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Score updated successfully"),
            @ApiResponse(responseCode = "404", description = "Score or question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<ScoreEntity> updateScoreForQuestion(@PathVariable int questionId, @RequestParam int scoreValue) {
        ScoreEntity score = scoreService.updateScoreForQuestion(questionId, scoreValue);
        return ResponseEntity.ok(score);
    }

    // Delete a ScoreEntity by id
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a score",
        description = "Deletes a score by its ID along with all associated user scores",
        responses = {
            @ApiResponse(responseCode = "200", description = "Score deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Score not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<String> deleteScoreEntity(@PathVariable int id) {
        String result = scoreService.deleteScoreEntity(id);
        return ResponseEntity.ok().body(result);
    }

    // Give Score to user
    @PostMapping("/award/questions/{questionId}/users/{userId}")
    @Operation(
        summary = "Award a score to a user for a question",
        description = "Validates the user's selected choice and awards the score if correct",
        responses = {
            @ApiResponse(responseCode = "200", description = "Score awarded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid choice",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Question or user not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:create')")
    public ResponseEntity<Void> awardScoreToUser(@PathVariable int questionId, @PathVariable int userId, @RequestParam int selectedChoiceId, @RequestParam double timeTakenSeconds) {
        scoreService.awardScoreToUser(questionId, userId, selectedChoiceId, timeTakenSeconds);
        return ResponseEntity.ok().build();
    }

    // Give Score to User for Translation Game
    @PostMapping("/award/translation/questions/{questionId}/users/{userId}")
    @Operation(
        summary = "Award a score for the translation game",
        description = "Validates the user's selected choices and awards the score if the order is correct",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "List of choice IDs selected by the user",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Score awarded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid choice order",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Question or user not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:create')")
    public ResponseEntity<Void> awardScoreForTranslationGame(@PathVariable int questionId, @PathVariable int userId, @RequestParam double timeTakenSeconds, @RequestBody List<Integer> userSelectedChoiceIds) {
        scoreService.awardScoreToUserForTranslationGame(questionId, userId, userSelectedChoiceIds, timeTakenSeconds);
        return ResponseEntity.ok().build();
    }
     
    // Total Live Score for User
    @GetMapping("/users/{userId}/total-live")
    @Operation(
        summary = "Get total live activity score for a user",
        description = "Calculates and retrieves the total score for a specific user's live activities only",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:read') or hasAuthority('teacher:read')")
    public ResponseEntity<Integer> getTotalLiveScoreForUser(@PathVariable int userId) {
        int totalLiveScore = scoreService.getTotalScoreForLiveUser(userId);
        return ResponseEntity.ok().body(totalLiveScore);
    }

    // Add this after the getTotalLiveScoreForUser method and before the leaderboard section
    @GetMapping("/live-activities/{liveActivityId}/total-score")
    @Operation(
        summary = "Get total score for a live activity",
        description = "Calculates and retrieves the total possible score for a specific live activity",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Live activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<Integer> getTotalScoreForLiveActivity(@PathVariable int liveActivityId) {
        int totalScore = scoreService.getTotalScoreForLiveActivity(liveActivityId);
        return ResponseEntity.ok().body(totalScore);
    }


    // Leaderboard
    @GetMapping("/live-activities/{activityId}/leaderboard")
    @Operation(
        summary = "Get live leaderboard for a live activity",
        description = "Returns the leaderboard (total scores per user) for a specific live activity",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<LeaderboardEntry>> getLiveActivityLeaderboard(@PathVariable int activityId) {
        List<LeaderboardEntry> leaderboard = scoreService.getLiveActivityLeaderboard(activityId);
        return ResponseEntity.ok().body(leaderboard);
    }

    //Scores in live activity
    @GetMapping("/live-activities/{activityId}/stats")
    @Operation(
        summary = "Get score statistics for a live activity",
        description = "Retrieves the average, lowest, and highest scores for a specific live activity",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<Map<String, Double>> getActivityScoreStatistics(@PathVariable int activityId) {
        Map<String, Double> statistics = scoreService.getActivityScoreStatistics(activityId);
        return ResponseEntity.ok().body(statistics);
    }
    
    // Recalculate and broadcast leaderboard
    @MessageMapping("/leaderboard/trigger/{activityId}")
    public void handleLeaderboardTrigger(@DestinationVariable int activityId) {
        // This method will be called when a client sends a message to /app/leaderboard/trigger/{activityId}
        scoreService.recalculateAndBroadcastLeaderboard(activityId);
    }


}