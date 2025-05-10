package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.LessonActivityEntity;
import edu.cit.lingguahey.Service.LessonActivityService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.UserActivityProjection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/lingguahey/activities")
@Tag(name = "Lesson Activity")
public class LessonActivityController {
    @Autowired
    LessonActivityService activityServ;

    // Create and assign to classroom and users
    @PostMapping("/classrooms/{classroomId}")
    @Operation(
        summary = "Create a new activity",
        description = "Creates a new activity and assigns it to all users in the specified classroom",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Activity data to create (without ID)",
            content = @Content(schema = @Schema(implementation = LessonActivityEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Activity created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
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
    public ResponseEntity<LessonActivityEntity> postActivityEntity(@RequestBody LessonActivityEntity activity, @PathVariable int classroomId) {
        LessonActivityEntity postActivity = activityServ.postActivityEntity(activity, classroomId);
        return ResponseEntity.status(201).body(postActivity);
    }

    // Read All Activities
    @GetMapping("")
    @Operation(
        summary = "Get all activities",
        description = "Retrieves a list of all activities",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<LessonActivityEntity>> getAllActivityEntity() {
        List<LessonActivityEntity> activities = activityServ.getAllActivityEntity();
        return ResponseEntity.ok().body(activities);
    }

    // Read Single Activity
    @GetMapping("/{id}")
    @Operation(
        summary = "Get an activity by ID",
        description = "Retrieves a specific activity by its ID",
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
    public ResponseEntity<LessonActivityEntity> getActivityEntity(@PathVariable int id) {
        LessonActivityEntity activity = activityServ.getActivityEntity(id);
        return ResponseEntity.ok().body(activity);
    }

    // Read all activities for user
    @GetMapping("/users/{userId}")
    @Operation(
        summary = "Get all activities for a user",
        description = "Retrieves all activities assigned to a specific user by their ID",
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
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:read')")
    public ResponseEntity<List<UserActivityProjection>> getAllActivitiesForUser(@PathVariable int userId) {
        List<UserActivityProjection> userActivities = activityServ.getAllActivitiesForUser(userId);
        return ResponseEntity.ok().body(userActivities);
    }

    // Read all activities for a classroom
    @GetMapping("/{classroomId}/activities")
    @Operation(
        summary = "Get all activities for a classroom",
        description = "Retrieves all activities assigned to a specific classroom by its ID",
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
    public ResponseEntity<List<LessonActivityEntity>> getAllActivitiesForClassroom(@PathVariable int classroomId) {
        List<LessonActivityEntity> activities = activityServ.getAllActivitiesForClassroom(classroomId);
        return ResponseEntity.ok().body(activities);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an activity",
        description = "Updates an existing activity by its ID",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated activity data",
            content = @Content(schema = @Schema(implementation = LessonActivityEntity.class))
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
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<LessonActivityEntity> putActivityEntity(@PathVariable int id, @RequestBody LessonActivityEntity newActivity) {
        LessonActivityEntity putActivity = activityServ.putActivityEntity(id, newActivity);
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

    // Mark Activity as Completed
    @PutMapping("/{id}/completed/{userId}")
    @Operation(
        summary = "Mark an activity as completed",
        description = "Marks an activity as completed for a specific user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity marked as completed"),
            @ApiResponse(responseCode = "404", description = "Activity or user not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:update')")
    public ResponseEntity<Void> markActivityAsCompleted(@PathVariable int id, @PathVariable int userId) {
        activityServ.markActivityAsCompleted(userId, id);
        return ResponseEntity.ok().build();
    }
}
