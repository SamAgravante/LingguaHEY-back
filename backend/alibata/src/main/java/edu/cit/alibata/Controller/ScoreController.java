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

import edu.cit.alibata.Entity.ScoreEntity;
import edu.cit.alibata.Service.ScoreService;
import edu.cit.alibata.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/alibata/scores")
@Tag(name = "Score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    // Create
    @PostMapping("")
    @Operation(
        description = "Create a new score",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Score data to create (without ID)",
            content = @Content(schema = @Schema(implementation = ScoreEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Score created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ScoreEntity postScoreEntity(@RequestBody ScoreEntity score) {
        return scoreService.postScoreEntity(score);
    }

    // Read All Scores
    @GetMapping("")
    @Operation(
        description = "Get all scores",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<ScoreEntity> getAllScoreEntity() {
        return scoreService.getAllScoreEntity();
    }

    // Read Single Score
    @GetMapping("/{id}")
    @Operation(
        description = "Get a score by ID",
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
    public ScoreEntity getScoreEntity(@PathVariable int id) {
        return scoreService.getScoreEntity(id);
    }

    // Update
    @PutMapping("")
    @Operation(
        description = "Update a score",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Score data to update (with ID)",
            content = @Content(schema = @Schema(implementation = ScoreEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Score updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Score not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ScoreEntity putScoreEntity(@RequestParam int id, @RequestBody ScoreEntity newScore) {
        return scoreService.putScoreEntity(id, newScore);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete a score by ID",
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
    public String deleteScoreEntity(@PathVariable int id) {
        return scoreService.deleteScoreEntity(id);
    }
}
