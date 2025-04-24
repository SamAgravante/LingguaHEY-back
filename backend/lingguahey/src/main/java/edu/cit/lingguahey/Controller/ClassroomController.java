package edu.cit.lingguahey.Controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Service.ClassroomService;
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/lingguahey/classrooms")
@Tag(name = "Classroom")
public class ClassroomController {
    
    @Autowired
    private ClassroomService classroomService;

    //Create
    @PostMapping("")
    @Operation(
        description = "Create a new classroom",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Classroom data to create (without ID)",
            content = @Content(schema = @Schema(implementation = ClassroomEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Classroom created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ClassroomEntity postClassroomEntity(@RequestBody ClassroomEntity classroom) {
        return classroomService.postClassroomEntity(classroom);
    }

    // Read All Classrooms
    @GetMapping("")
    @Operation(
        description = "Get all classrooms",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<ClassroomEntity> getAllClassroomEntity() {
        return classroomService.getAllClassroomEntity();
    }

    // Read Single Classroom
    @GetMapping("/{id}")
    @Operation(
        description = "Get a classroom by ID",
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
    public ClassroomEntity getClassroomEntity(@PathVariable int id) throws AccessDeniedException {
        return classroomService.getClassroomEntity(id);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        description = "Update a classroom",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Classroom data to update (with ID)",
            content = @Content(schema = @Schema(implementation = ClassroomEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Classroom updated successfully"),
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
    public ClassroomEntity putClassroomEntity(@PathVariable int id, @RequestBody ClassroomEntity newClassroom) {
        return classroomService.putClassroomEntity(id, newClassroom);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete a classroom by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Classroom deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Classroom not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public String deleteClassroomEntity(@PathVariable int id) {
        return classroomService.deleteClassroomEntity(id);
    }
    
}
