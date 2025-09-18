package edu.cit.lingguahey.Controller;

import java.util.Map;

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

import edu.cit.lingguahey.Service.PotionShopService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.PotionPurchaseRequest;
//import edu.cit.lingguahey.model.PotionUseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/potion-shop")
@Tag(name = "Potion Shop")
public class PotionShopController {
    
    @Autowired
    private PotionShopService potionShopServ;

    // Buy Potion
    @PostMapping("/buy")
    @Operation(
        summary = "Buy a potion", 
        description = "Allows a user to buy a potion using coins.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Potion purchased successfully."),
            @ApiResponse(responseCode = "400", description = "Not enough coins or invalid potion type.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#request.userId == principal.userId")
    public ResponseEntity<?> buyPotion(@RequestBody PotionPurchaseRequest request) {
        try {
            potionShopServ.buyPotion(request.getUserId(), request.getPotionType(), request.getCost());
            return ResponseEntity.ok("Potion purchased successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Purchase failed", e.getMessage()));
        }
    }

    // Get All Potions for a user
    @GetMapping("/potions/{userId}")
    @Operation(
        summary = "Get a user's potion stock",
        description = "Retrieves a map of all potions and their quantities for a specified user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved potion stock."),
            @ApiResponse(responseCode = "404", description = "User not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#userId == principal.userId")
    public ResponseEntity<?> getPotions(@PathVariable int userId) {
        try {
            Map<String, Integer> potions = potionShopServ.getPotions(userId);
            return ResponseEntity.ok(potions);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to retrieve potions", e.getMessage()));
        }
    }
    
}
