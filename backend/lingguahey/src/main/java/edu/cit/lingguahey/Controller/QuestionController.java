package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.lingguahey.Entity.QuestionEntity;
import edu.cit.lingguahey.Service.QuestionService;
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/lingguahey/questions")
@Tag(name = "Question")
public class QuestionController {

    @Autowired
    private QuestionService questionServ;

    // Create question for activity
    @PostMapping(value = "/activities/{activityId}", consumes = "multipart/form-data")
    @Operation(
        summary = "Create a question for a specific activity with an image",
    description = "Creates a new question and associates it with a specific activity, optionally uploading an image",
        /*requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Question data to create (without ID)",
            content = @Content(schema = @Schema(implementation = QuestionEntity.class))
        ),*/
        responses = {
            @ApiResponse(responseCode = "201", description = "Question created and added to activity successfully"),
            @ApiResponse(responseCode = "404", description = "Activity not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<QuestionEntity> postQuestionForActivity(@PathVariable int activityId, @RequestParam String questionDescription, @RequestParam String questionText, @RequestParam(value = "image", required = false) MultipartFile image) {
        QuestionEntity postQuestion = questionServ.postQuestionForActivity(activityId, questionDescription, questionText, image);
        return ResponseEntity.status(201).body(postQuestion);
    }

    // Read All Questions
    @GetMapping("")
    @Operation(
        summary = "Get all questions",
        description = "Retrieves a list of all questions",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<QuestionEntity>> getAllQuestionsEntity() {
        List<QuestionEntity> questions = questionServ.getAllQuestionEntity();
        return ResponseEntity.ok().body(questions);
    }

    // Read Single Question
    @GetMapping("/{id}")
    @Operation(
        summary = "Get a question by ID",
        description = "Retrieves a specific question by its ID",
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
    public ResponseEntity<QuestionEntity> getQuestionEntity(@PathVariable int id) {
        QuestionEntity question = questionServ.getQuestionEntity(id);
        return ResponseEntity.ok().body(question);
    }

    // Read all questions for activity
    @GetMapping("/activities/{activityId}")
    @Operation(
        summary = "Get all questions for an activity",
        description = "Retrieves all questions associated with a specific activity",
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
    public ResponseEntity<List<QuestionEntity>> getQuestionsForActivity(@PathVariable int activityId) {
        List<QuestionEntity> questions = questionServ.getQuestionsForActivity(activityId);
        return ResponseEntity.ok().body(questions);
    }

    // Update
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @Operation(
        summary = "Update a question",
        description = "Updates an existing question by its ID",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated question data",
            content = @Content(schema = @Schema(implementation = QuestionEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<QuestionEntity> putQuestionEntity(
            @PathVariable int id,
            @RequestParam String questionDescription,
            @RequestParam String questionText,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        QuestionEntity putQuestion = questionServ.putQuestionEntity(id, questionDescription, questionText, image);
        return ResponseEntity.ok().body(putQuestion);
    }

    // Delete a QuestionEntity by id
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a question",
        description = "Deletes a question by its ID along with all associated choices",
        responses = {
            @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<String> deleteQuestionEntity(@PathVariable int id) {
        String result = questionServ.deleteQuestionEntity(id);
        return ResponseEntity.ok().body(result);
    }
}
