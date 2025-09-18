package edu.cit.lingguahey.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Service.GameService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.GameSession;
import edu.cit.lingguahey.model.GuessRequest;
import edu.cit.lingguahey.model.GuessResponse;
import edu.cit.lingguahey.model.MonsterResponse;
import edu.cit.lingguahey.model.PotionUseRequest;
import edu.cit.lingguahey.model.PotionUseResponse;
import edu.cit.lingguahey.model.StartGameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/game")
@Tag(name = "Game")
public class GameController {

    @Autowired
    private GameService gameServ;

    // Start new Game
    @PostMapping("/start")
    @Operation(
        summary = "Start a new game session",
        description = "Initializes a new game session for a user on a specified level.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Game session started successfully.",
                content = @Content(schema = @Schema(implementation = GameSession.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or user has no lives.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Level or user not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#request.userId == principal.userId")
    public ResponseEntity<?> initializeGame(@RequestBody StartGameRequest request) {
        try {
            gameServ.initializeGame(request.getLevelId(), request.getUserId());
            return ResponseEntity.ok(gameServ.getCurrentGameSessionResponse());
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Game start failed", e.getMessage()));
        }
    }

    // Read Current Monster
    @GetMapping("/current-monster")
    @Operation(
        summary = "Get the current monster",
        description = "Retrieves the current monster for the active game session, including jumbled letters for the puzzle.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current monster.",
                content = @Content(schema = @Schema(implementation = MonsterResponse.class))),
            @ApiResponse(responseCode = "400", description = "No active game session.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> getCurrentMonster() {
        try {
            MonsterResponse currentMonster = gameServ.getCurrentMonster();
            return ResponseEntity.ok(currentMonster);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Game state error", e.getMessage()));
        }
    }

    // Process Guess
    @PostMapping("/guess")
    @Operation(
        summary = "Submit a guess",
        description = "Submits the user's guess for the current monster and processes the game logic.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Guess processed successfully. Returns feedback and updated game state.",
                content = @Content(schema = @Schema(implementation = GuessResponse.class))),
            @ApiResponse(responseCode = "400", description = "No active game session.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> submitGuess(@RequestBody GuessRequest guessRequest) {
        try {
            GuessResponse response = gameServ.processGuess(guessRequest.getGuessedName());
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Game state error", e.getMessage()));
        }
    }

    // Use Potion with turn-based logic
    @PostMapping("/use-potion")
    @Operation(
        summary = "Use a potion", 
        description = "Consumes a potion from a user's inventory to apply an in-game effect.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Potion used successfully. Returns the updated game session.",
                content = @Content(schema = @Schema(implementation = GameSession.class))),
            @ApiResponse(responseCode = "400", description = "Invalid potion or user has no potions.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Potion cannot be used at this time.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @PreAuthorize("#request.userId == principal.userId")
    public ResponseEntity<?> usePotion(@RequestBody PotionUseRequest request) {
        try {
            PotionUseResponse response = gameServ.usePotion(request.getUserId(), request.getPotionType());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Entity not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Action failed", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Conflict", e.getMessage()));
        }
    }
}
