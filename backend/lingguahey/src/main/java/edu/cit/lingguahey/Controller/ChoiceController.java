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

import edu.cit.lingguahey.Entity.ChoiceEntity;
import edu.cit.lingguahey.Service.ChoiceService;
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/lingguahey/choices")
@Tag(name = "Choice")
public class ChoiceController {

    @Autowired
    private ChoiceService choiceServ;

    // Create and Add Choice to Question
    @PostMapping("/questions/{questionId}")
    @Operation(
        summary = "Create a choice for a specific question",
        description = "Creates a new choice and associates it with a specific question",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Choice data to create (without ID)",
            content = @Content(schema = @Schema(implementation = ChoiceEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Choice created and added to question successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('teacher:create')")
    public ResponseEntity<ChoiceEntity> postChoiceForQuestion(@PathVariable int questionId, @RequestBody ChoiceEntity choice) {
        ChoiceEntity postChoice = choiceServ.postChoiceForQuestion(questionId, choice);
        return ResponseEntity.status(201).body(postChoice);
    }

    // Read All Choices
    @GetMapping("")
    @Operation(
        summary = "Get all choices",
        description = "Retrieves a list of all choices",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<ChoiceEntity>> getAllChoiceEntity() {
        List<ChoiceEntity> choices = choiceServ.getAllChoiceEntity();
        return ResponseEntity.ok().body(choices);
    }

    // Read Single Choice
    @GetMapping("/{id}")
    @Operation(
        summary = "Get a choice by ID",
        description = "Retrieves a specific choice by its ID",
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
    public ResponseEntity<ChoiceEntity> getChoiceEntity(@PathVariable int id) {
        ChoiceEntity choice = choiceServ.getChoiceEntity(id);
        return ResponseEntity.ok().body(choice);
    }

    // Read all choices for question
    @GetMapping("/questions/{questionId}")
    @Operation(
        summary = "Get all choices for a question",
        description = "Retrieves all choices associated with a specific question",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<ChoiceEntity>> getChoicesForQuestion(@PathVariable int questionId) {
        List<ChoiceEntity> choices = choiceServ.getChoicesForQuestion(questionId);
        return ResponseEntity.ok().body(choices);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a choice",
        description = "Updates an existing choice by its ID",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated choice data",
            content = @Content(schema = @Schema(implementation = ChoiceEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Choice updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
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
    public ResponseEntity<ChoiceEntity> putChoiceEntity(@PathVariable int id, @RequestBody ChoiceEntity newChoice) {
        ChoiceEntity putChoice = choiceServ.putChoiceEntity(id, newChoice);
        return ResponseEntity.ok().body(putChoice);
    }

    // Delete a ChoiceEntity by id
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a choice",
        description = "Deletes a choice by its ID",
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
    public ResponseEntity<String> deleteChoiceEntity(@PathVariable int id) {
        String result = choiceServ.deleteChoiceEntity(id);
        return ResponseEntity.ok().body(result);
    }

    // Validate user's choices for translation game
    @PostMapping("/questions/{questionId}/validate-translation")
    @Operation(
        summary = "Validate user's choices for translation game",
        description = "Validates if the user's selected choices are in the correct order for the translation game",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "List of choice IDs selected by the user",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Validation result"),
            @ApiResponse(responseCode = "404", description = "Question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:create')")
    public ResponseEntity<Boolean> validateTranslationGame(@PathVariable int questionId, @RequestBody List<Integer> userSelectedChoiceIds) {
        boolean isCorrect = choiceServ.validateTranslationGame(questionId, userSelectedChoiceIds);
        return ResponseEntity.ok().body(isCorrect);
    }
}

