package edu.cit.lingguahey.Controller;

import java.nio.file.AccessDeniedException;
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

import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Service.ClassroomService;
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

// IF GA WONDER KA KUNG NAAY GUBA DIRI
// AKO GI COMMENT OUT ANG DELETE CLASSROOM
// UG ADD STUDENT TO CLASSROOM
// KAY NEED PANA E DOUBLE CHECK TUNGOD SA
// LESSON ACTIVITY

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
            @ApiResponse(responseCode = "403", description = "Forbidden: Teacher has reached the classroom limit for their subscription plan",
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
            @ApiResponse(responseCode = "404", description = "Classroom not found",
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

    // Read by Teacher ID
    @GetMapping("/teacher/{teacherId}")
    @Operation(
        summary = "Get all classrooms by teacher ID",
        description = "Retrieve all classrooms for a teacher ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Classroom not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#teacherId == principal.userId or hasAuthority('admin:read')")
    public ResponseEntity<List<ClassroomEntity>> getClassroomsByTeacher(@PathVariable int teacherId) {
        List<ClassroomEntity> classrooms = classroomService.getClassroomByTeacherId(teacherId);
        return ResponseEntity.ok(classrooms);
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
    @Transactional
    public ClassroomEntity putClassroomEntity(@PathVariable int id, @RequestBody ClassroomEntity newClassroom) {
        return classroomService.putClassroomEntity(id, newClassroom);
    }

    // Delete a ClassroomEntity by id
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

    // Add Student to Classroom
    @PostMapping("/{classroomId}/students/{studentId}")
    @Operation(
        description = "Add a student to a classroom",
        responses = {
            @ApiResponse(responseCode = "200", description = "Student added successfully to the classroom"),
            @ApiResponse(responseCode = "404", description = "Classroom or student not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public String addStudentToClassroom(@PathVariable int classroomId, @PathVariable int studentId) throws AccessDeniedException {
        return classroomService.addStudentToClassroom(classroomId, studentId);
    }

    // Remove User from Classroom
    @DeleteMapping("/{classroomId}/students/{studentId}")
    @Operation(
        summary = "Remove a student from a classroom",
        description = "Removes the association between a student and a classroom",
        responses = {
            @ApiResponse(responseCode = "200", description = "Student removed successfully"),
            @ApiResponse(responseCode = "404", description = "Classroom or student not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<String> removeStudentFromClassroom(@PathVariable int classroomId, @PathVariable int studentId) throws AccessDeniedException {
        String result = classroomService.removeStudentFromClassroom(classroomId, studentId);
        return ResponseEntity.ok(result);
    }

    // Read all students for a classroom
    @GetMapping("/{classroomId}/students")
    @Operation(
        description = "Get all students for a classroom",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of students retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Classroom not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public List<UserEntity> getAllStudentsForClassroom(@PathVariable int classroomId) {
        return classroomService.getAllStudentsForClassroom(classroomId);
    }

    // Find classroom by userId
    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get classroom by user ID",
        description = "Retrieves the classroom associated with a specific user by their ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Classroom retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Classroom not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId or hasAuthority('admin:read')")
    public ResponseEntity<ClassroomEntity> getClassroomByUserId(@PathVariable int userId) {
        ClassroomEntity classroom = classroomService.getClassroomByUserId(userId);
        return ResponseEntity.ok(classroom);
    }
    
}
