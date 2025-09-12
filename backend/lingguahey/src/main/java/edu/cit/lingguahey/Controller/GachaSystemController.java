package edu.cit.lingguahey.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Service.GachaSystemService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.GachaPullRequest;
import edu.cit.lingguahey.model.GachaPullResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/gacha")
@Tag(name = "Gacha")
public class GachaSystemController {
    
    @Autowired
    private GachaSystemService gachaSystemServ;

    // Gacha Pull
    @PostMapping("/pull")
    @Operation(
        summary = "Perform a gacha pull for a user",
        description = "Deducts gems from a user's account and gives them a random cosmetic based on rarity chances.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The user's ID to perform the gacha pull.",
            required = true,
            content = @Content(schema = @Schema(implementation = GachaPullRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Pull successful, returns the pulled cosmetic.",
                content = @Content(schema = @Schema(implementation = GachaPullResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Not enough gems.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: User not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#request.userId == principal.userId")
    public ResponseEntity<?> performGachaPull(@RequestBody GachaPullRequest request) {
        try {
            GachaPullResponse response = gachaSystemServ.performGachaPull(request.getUserId());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Not enough gems", e.getMessage()));
        }
    }
}
