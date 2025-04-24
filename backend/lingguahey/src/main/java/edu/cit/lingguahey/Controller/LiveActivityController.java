package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import edu.cit.lingguahey.model.ErrorResponse;
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
    @PostMapping("")
    @Operation(
        description = "Create a new activity",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Activity data to create (without ID)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @Schema(implementation = LiveActivityEntity.class, hidden = true) // Hide ID in example
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public LiveActivityEntity postActivityEntity(@RequestBody LiveActivityEntity activity) {
        return activityServ.postActivityEntity(activity);
    }

    // Read All Activities
    @GetMapping("")
    @Operation(
        description = "Get all activities",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<LiveActivityEntity> getAllActivityEntity() {
        return activityServ.getAllActivityEntity();
    }

    // Read Single Activity
    @GetMapping("/{id}")
    @Operation(
        description = "Get an activity by ID",
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
    public LiveActivityEntity getActivityEntity(@PathVariable int id) {
        return activityServ.getActivityEntity(id);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        description = "Update an activity",
        responses = {
            @ApiResponse(responseCode = "200", description = "Activity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
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
    public LiveActivityEntity putActivityEntity(@PathVariable int id, @RequestBody LiveActivityEntity newActivity) {
        return activityServ.putActivityEntity(id, newActivity);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete an activity by ID",
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
    public String deleteActivityEntity(@PathVariable int id) {
        return activityServ.deleteActivityEntity(id);
    }
}
