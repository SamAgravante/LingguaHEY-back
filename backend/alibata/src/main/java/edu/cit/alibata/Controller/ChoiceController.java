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

import edu.cit.alibata.Entity.ChoiceEntity;
import edu.cit.alibata.Service.ChoiceService;
import edu.cit.alibata.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/alibata/choices")
@Tag(name = "Choice")
public class ChoiceController {

    @Autowired
    private ChoiceService choiceService;

    // Create
    @PostMapping("")
    @Operation(
        description = "Create a new choice",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Choice data to create (without ID)",
            content = @Content(schema = @Schema(implementation = ChoiceEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Choice created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ChoiceEntity postChoiceEntity(@RequestBody ChoiceEntity choice) {
        return choiceService.postChoiceEntity(choice);
    }

    // Read All Choices
    @GetMapping("")
    @Operation(
        description = "Get all choices",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<ChoiceEntity> getAllChoiceEntity() {
        return choiceService.getAllChoiceEntity();
    }

    // Read Single Choice
    @GetMapping("/{id}")
    @Operation(
        description = "Get a choice by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Choice not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ChoiceEntity getChoiceEntity(@PathVariable int id) {
        return choiceService.getChoiceEntity(id);
    }

    // Update
    @PutMapping("")
    @Operation(
        description = "Update a choice",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Choice data to update (with ID)",
            content = @Content(schema = @Schema(implementation = ChoiceEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Choice updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Choice not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ChoiceEntity putChoiceEntity(@RequestParam int id, @RequestBody ChoiceEntity newChoice) {
        return choiceService.putChoiceEntity(id, newChoice);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete a choice by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Choice deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Choice not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public String deleteChoiceEntity(@PathVariable int id) {
        return choiceService.deleteChoiceEntity(id);
    }
}

