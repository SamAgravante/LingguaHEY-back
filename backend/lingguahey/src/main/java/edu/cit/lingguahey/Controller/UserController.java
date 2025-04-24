package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Service.UserService;
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/lingguahey/users")
@Tag(name = "User")
public class UserController {
    
    @Autowired
    UserService userServ;

    // Create
    @PostMapping("")
    @Operation(
        description = "Create a new user",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User data to create (without ID)",
            content = @Content(schema = @Schema(implementation = UserEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public UserEntity postUserEntity(@RequestBody UserEntity user){
        return userServ.postUserEntity(user);
    }

    // Read All Users
    @GetMapping("")
    @Operation(
        description = "Get all users",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("hasAuthority('admin:read')")
    public List<UserEntity> getAllUserEntity(){
        return userServ.getAllUserEntity();
    }

    // Read Single User
    @GetMapping("/{id}")
    @Operation(
        description = "Get a user by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:read')")
    public UserEntity getUserEntity(@PathVariable int id){
        return userServ.getUserEntity(id);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        description = "Update a user",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User data to update (with ID)",
            content = @Content(schema = @Schema(implementation = UserEntity.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:update')")
    public UserEntity putUserEntity(@PathVariable int id, @RequestBody UserEntity newUserEntity){
        return userServ.putUserEntity(id, newUserEntity);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete a user by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:delete')")
    public String deleteUserEntity(@PathVariable int id){
        return userServ.deleteUserEntity(id);
    }
}
