package edu.cit.alibata.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.alibata.Entity.ActivityEntity;
import edu.cit.alibata.Service.ActivityService;
import edu.cit.alibata.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/alibata/activities")
@Tag(name = "Activity")
public class ActivityController {

    @Autowired
    ActivityService activityServ;

    // Create
    @PostMapping("")
    @Operation(
        description = "Create a new activity",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Activity data to create (without ID)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @Schema(implementation = ActivityEntity.class, hidden = true) // Hide ID in example
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
    public ActivityEntity postActivityEntity(@RequestBody ActivityEntity activity) {
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
    public List<ActivityEntity> getAllActivityEntity() {
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
    public ActivityEntity getActivityEntity(@PathVariable int id) {
        return activityServ.getActivityEntity(id);
    }

    // Update
    @PutMapping("")
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
    public ActivityEntity putActivityEntity(@RequestParam int id, @RequestBody ActivityEntity newActivity) {
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
