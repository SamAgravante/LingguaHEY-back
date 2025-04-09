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

import edu.cit.alibata.Entity.QuestionEntity;
import edu.cit.alibata.Service.QuestionService;
import edu.cit.alibata.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/alibata/questions")
@Tag(name = "Question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Create
    @PostMapping("")
    @Operation(
        description = "Create a new question",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Question data to create (without ID)",
            content = @Content(schema = @Schema(implementation = QuestionEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Question created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public QuestionEntity postQuestionEntity(@RequestBody QuestionEntity question) {
        return questionService.postQuestionEntity(question);
    }

    // Read All Questions
    @GetMapping("")
    @Operation(
        description = "Get all questions",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<QuestionEntity> getAllQuestionEntity() {
        return questionService.getAllQuestionEntity();
    }

    // Read Single Question
    @GetMapping("/{id}")
    @Operation(
        description = "Get a question by ID",
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
    public QuestionEntity getQuestionEntity(@PathVariable int id) {
        return questionService.getQuestionEntity(id);
    }

    // Update
    @PutMapping("")
    @Operation(
        description = "Update a question",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Question data to update (with ID)",
            content = @Content(schema = @Schema(implementation = QuestionEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
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
    public QuestionEntity putQuestionEntity(@RequestParam int id, @RequestBody QuestionEntity newQuestion) {
        return questionService.putQuestionEntity(id, newQuestion);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete a question by ID",
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
    public String deleteQuestionEntity(@PathVariable int id) {
        return questionService.deleteQuestionEntity(id);
    }
}
