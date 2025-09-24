package edu.cit.lingguahey.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityNotFoundException;

import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Service.UserService;
import edu.cit.lingguahey.model.EquippedCosmeticResponse;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.PasswordResetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/lingguahey/users")
@Tag(name = "User")
public class UserController {
    
    @Autowired
    UserService userServ;

    // Create
    @PostMapping("")
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user in the system",
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
    // access
    @PreAuthorize("hasAuthority('admin:create')")
    public UserEntity postUserEntity(@RequestBody UserEntity user){
        return userServ.postUserEntity(user);
    }

    // Read All Users
    @GetMapping("")
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    // access
    @PreAuthorize("hasAuthority('admin:read') or hasAuthority('teacher:read')")
    public List<UserEntity> getAllUserEntity(){
        return userServ.getAllUserEntity();
    }

    // Read Single User
    @GetMapping("/{id}")
    @Operation(
        summary = "Get a user by ID",
        description = "Retrieves a specific user by their ID",
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
    // access
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:read') or hasAuthority('teacher:read')")
    public UserEntity getUserEntity(@PathVariable int id){
        return userServ.getUserEntity(id);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a user",
        description = "Updates an existing user's information by their ID",
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
    // access
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:update')")
    public UserEntity putUserEntity(@PathVariable int id, @RequestBody UserEntity newUserEntity){
        return userServ.putUserEntity(id, newUserEntity);
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by their ID",
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
    // access
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:delete')")
    public String deleteUserEntity(@PathVariable int id){
        return userServ.deleteUserEntity(id);
    }

    // Update Subscription Status
    @PutMapping("/update-subscription/{id}")
    @Operation(
        summary = "Update a subscription status",
        description = "Updates the subscription status information by their ID",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Subscription update data",
            content = @Content(schema = @Schema(implementation = SubscriptionUpdateRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully"),
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
    @Transactional
    public ResponseEntity<?> updateSubscriptionStatus(
            @PathVariable int id,
            @RequestBody SubscriptionUpdateRequest request) {
        try {
            userServ.getUserEntity(id);
            userServ.updateSubscriptionStatus(
                id, 
                request.isSubscriptionStatus(), 
                request.getSubscriptionType()
            );
            UserEntity updatedUser = userServ.getUserEntity(id);
            
            return ResponseEntity.ok()
                .body(Map.of(
                    "message", "Subscription updated successfully",
                    "userId", updatedUser.getUserId(),
                    "subscriptionStatus", updatedUser.getSubscriptionStatus(),
                    "subscriptionType", updatedUser.getSubscriptionType(),
                    "startDate", updatedUser.getSubscriptionStartDate(),
                    "endDate", updatedUser.getSubscriptionEndDate()
                ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error updating subscription status: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/reset-password")
    @Operation(
        summary = "Reset a user's password",
        description = "Verifies the old password and, if correct, updates to the new password",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON with oldPassword and newPassword",
            required = true,
            content = @Content(schema = @Schema(implementation = PasswordResetRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or old password incorrect",
                content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema()))
        }
    )
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:update')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable("id") int id,
            @Valid @RequestBody PasswordResetRequest payload) {

        userServ.resetPassword(id, payload.getOldPassword(), payload.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics/active-token-count")
    @Operation(
        summary = "Get count of active tokens",
        description = "Retrieves the total number of unexpired and unrevoked tokens, useful for active user analytics.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of active token count",
                content = @Content(schema = @Schema(type = "integer", format = "int64"))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<Long> getActiveTokenCount() {
        long activeTokens = userServ.getActiveTokenCount();
        return ResponseEntity.ok(activeTokens);
    }

    // Get Equipped Cosmetic
    @GetMapping("/{id}/equipped-cosmetic")
    @Operation(
        summary = "Get a user's equipped cosmetic",
        description = "Retrieves the currently equipped cosmetic item for the specified user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved equipped cosmetic",
                content = @Content(schema = @Schema(implementation = EquippedCosmeticResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#id == principal.userId or hasAuthority('admin:read')")
    public ResponseEntity<EquippedCosmeticResponse> getEquippedCosmetic(@PathVariable int id) {
        UserEntity user = userServ.getUserEntity(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        EquippedCosmeticResponse response = new EquippedCosmeticResponse();
        if (user.getEquippedCosmetic() != null) {
            response.setEquippedCosmetic(user.getEquippedCosmetic());
        } else {
            response.setEquippedCosmetic(null);
        }
        return ResponseEntity.ok(response);
    }
}
