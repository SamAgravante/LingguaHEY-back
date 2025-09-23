package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.LiveActivityEntity;
import edu.cit.lingguahey.Service.LiveActivityService;
import edu.cit.lingguahey.model.ClassroomActivityLiveProjection;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.UserActivityLiveProjection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/lingguahey/live-activities")
@Tag(name = "Live Activity")
public class LiveActivityController {

    @Autowired
    LiveActivityService activityServ;

    // Create
    @PostMapping("/classrooms/{classroomId}")
    @Operation(
        summary = "Create a new live activity",
        description = "Creates a new live activity and assigns it to all users in the specified classroom",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Live activity data to create (without ID)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @Schema(implementation = LiveActivityEntity.class, hidden = true) // Hide ID in example
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Live activity created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Classroom not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<LiveActivityEntity> postActivityEntity(@RequestBody LiveActivityEntity activity, @PathVariable int classroomId) {
        LiveActivityEntity postActivity = activityServ.postActivityEntity(activity, classroomId);
        return ResponseEntity.status(201).body(postActivity);
    }

    // Read All Activities
    @GetMapping("")
    @Operation(
        summary = "Get all live activities",
        description = "Retrieves a list of all live activities",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<LiveActivityEntity>> getAllActivityEntity() {
        List<LiveActivityEntity> activities = activityServ.getAllActivityEntity();
        return ResponseEntity.ok().body(activities);
    }

    // Read Single Activity
    @GetMapping("/{id}")
    @Operation(
        summary = "Get a live activity by ID",
        description = "Retrieves a specific live activity by its ID",
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
    public ResponseEntity<LiveActivityEntity> getActivityEntity(@PathVariable int id) {
        LiveActivityEntity activity = activityServ.getActivityEntity(id);
        return ResponseEntity.ok().body(activity);
    }

    // Read all activities for a user
    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get all live activities for a user",
        description = "Retrieves all live activities assigned to a specific user by their ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of activities retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<UserActivityLiveProjection>> getAllActivitiesForUser(@PathVariable int userId) {
        List<UserActivityLiveProjection> activities = activityServ.getAllActivitiesForUser(userId);
        return ResponseEntity.ok().body(activities);
    }

    // Read all live activities for a classroom
    @GetMapping("/{classroomId}/live-activities")
    @Operation(
        summary = "Get all live activities for a classroom",
        description = "Retrieves all live activities assigned to a specific classroom by its ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of activities retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Classroom not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<ClassroomActivityLiveProjection>> getAllActivitiesForClassroom(@PathVariable int classroomId) {
        List<ClassroomActivityLiveProjection> activities = activityServ.getAllActivitiesForClassroom(classroomId);
        return ResponseEntity.ok().body(activities);
    }

    // Read deployed activity for a classroom
    @GetMapping("/classrooms/{classroomId}/deployed")
    @Operation(
        summary = "Get the single deployed live activity for a classroom",
        description = "Retrieves the live activity currently marked as deployed for a specific classroom. " +
                      "It's assumed there is only one deployed activity per classroom.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Deployed live activity retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Classroom not found or no deployed activity for classroom",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<Integer> getDeployedActivityForClassroom(@PathVariable int classroomId) {
        int deployedActivityId = activityServ.getDeployedActivityForClassroom(classroomId);
        return ResponseEntity.ok().body(deployedActivityId);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a live activity",
        description = "Updates an existing live activity by its ID",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated activity data",
            content = @Content(schema = @Schema(implementation = LiveActivityEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<LiveActivityEntity> putActivityEntity(@PathVariable int id, @RequestBody LiveActivityEntity newActivity) {
        LiveActivityEntity putActivity = activityServ.putActivityEntity(id, newActivity);
        return ResponseEntity.ok().body(putActivity);
    }

    // Delete an ActivityEntity by id
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an activity",
        description = "Deletes an activity by its ID along with all associated user activities",
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<String> deleteActivityEntity(@PathVariable int id) {
        String result = activityServ.deleteActivityEntity(id);
        return ResponseEntity.ok().body(result);
    }

    // Update activity deployed status
    @PutMapping("/{activityId}/set-deployed/{deployStatus}")
    @Operation(
        summary = "Set the deployment status of a live activity",
        description = "Sets the 'isDeployed' status of a live activity. " +
                      "If setting to 'true', any other currently deployed activity in the same classroom will be undeployed.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity deployment status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Live activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<LiveActivityEntity> setActivityDeployedStatus(@PathVariable int activityId, @PathVariable boolean deployStatus) {
        LiveActivityEntity updatedActivity = activityServ.setActivityDeployedStatus(activityId, deployStatus);
        return ResponseEntity.ok().body(updatedActivity);
    }
}
