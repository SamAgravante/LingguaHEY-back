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

import edu.cit.alibata.Entity.StoryEntity;
import edu.cit.alibata.Service.StoryService;
import edu.cit.alibata.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/alibata/stories")
@Tag(name = "Story")
public class StoryController {

    @Autowired
    private StoryService storyService;

    // Create a new StoryEntity
    @PostMapping("")
    @Operation(
        description = "Create a new story",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Story data to create (without ID)",
            content = @Content(schema = @Schema(implementation = StoryEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Story created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public StoryEntity postStoryEntity(@RequestBody StoryEntity story) {
        return storyService.postStoryEntity(story);
    }

    // Retrieve all StoryEntities
    @GetMapping("")
    @Operation(
        description = "Get all stories",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<StoryEntity> getAllStoryEntity() {
        return storyService.getAllStoryEntity();
    }

    // Retrieve a single StoryEntity by id
    @GetMapping("/{id}")
    @Operation(
        description = "Get a story by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Story not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public StoryEntity getStoryEntity(@PathVariable int id) {
        return storyService.getStoryEntity(id);
    }

    // Update an existing StoryEntity
    @PutMapping("")
    @Operation(
        description = "Update a story",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Story data to update (with ID)",
            content = @Content(schema = @Schema(implementation = StoryEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Story updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Story not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public StoryEntity putStoryEntity(@RequestParam int id, @RequestBody StoryEntity newStory) {
        return storyService.putStoryEntity(id, newStory);
    }

    // Delete a StoryEntity by id
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete a story by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Story deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Story not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public String deleteStoryEntity(@PathVariable int id) {
        return storyService.deleteStoryEntity(id);
    }
}

