package edu.cit.lingguahey.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Service.PotionShopService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.PotionPurchaseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/potion-shop")
@Tag(name = "Gacha")
public class PotionShopController {
    
    @Autowired
    private PotionShopService potionShopServ;

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
}
