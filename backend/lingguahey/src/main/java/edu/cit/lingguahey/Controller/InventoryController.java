package edu.cit.lingguahey.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import edu.cit.lingguahey.Service.InventoryService;
import edu.cit.lingguahey.model.EquipCosmeticRequest;
import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/inventory")
@Tag(name = "Inventory")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryServ;

    // Read Inventory by id
    @GetMapping("/{userId}")
    @Operation(
        summary = "View user's inventory", 
        description = "Retrieves all cosmetics owned by a user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory."),
            @ApiResponse(responseCode = "404", description = "User not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId")
    public ResponseEntity<?> getUserInventory(@PathVariable int userId) {
        try {
            List<CosmeticEntity> inventory = inventoryServ.getUserInventory(userId);
            return ResponseEntity.ok(inventory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found", e.getMessage()));
        }
    }

    // Equip Cosmetic by id
    @PostMapping("/equip")
    @Operation(
        summary = "Equip a cosmetic", 
        description = "Equips a cosmetic from the user's inventory.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cosmetic equipped successfully."),
            @ApiResponse(responseCode = "404", description = "User or cosmetic not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "User does not own this cosmetic.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#request.userId == principal.userId")
    public ResponseEntity<?> equipCosmetic(@RequestBody EquipCosmeticRequest request) {
        try {
            inventoryServ.equipCosmetic(request.getUserId(), request.getCosmeticId());
            return ResponseEntity.ok("Cosmetic equipped successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Entity not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Action failed", e.getMessage()));
        }
    }
}
