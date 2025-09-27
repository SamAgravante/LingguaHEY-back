package edu.cit.lingguahey.Controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.Entity.LevelEntity;
import edu.cit.lingguahey.Entity.MonsterEntity;
import edu.cit.lingguahey.Entity.MonsterType;
import edu.cit.lingguahey.Service.LevelService;
import edu.cit.lingguahey.model.CompletedLevelResponse;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.LevelCreateRequest;
import edu.cit.lingguahey.model.LevelEditRequest;
import edu.cit.lingguahey.model.LevelMonsterResponse;
import edu.cit.lingguahey.model.LevelResponse;
import edu.cit.lingguahey.model.MonsterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/levels")
@Tag(name = "Levels")
public class LevelController {

    @Autowired
    private LevelService levelServ;

    // Create
    @PostMapping("")
    @Operation(
        summary = "Create a new level", 
        description = "Allows an admin to create a new level with a list of monsters.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Level created successfully.", 
                content = @Content(schema = @Schema(implementation = LevelResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or level creation failed.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "A level with the same name already exists.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> createLevel(@RequestBody LevelCreateRequest request) {
        try {
            LevelEntity newLevel = levelServ.createLevel(request);
            LevelResponse response = convertToDto(newLevel);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Failed to create level", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Failed to create level", "A level with the same name already exists."));
        }
    }


    // Read all
    @GetMapping("")
    @Operation(
        summary = "Get all levels", 
        description = "Retrieves a list of all existing levels.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of levels.", 
                content = @Content(schema = @Schema(implementation = LevelResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<LevelResponse>> getAllLevels() {
        List<LevelEntity> levels = levelServ.getAllLevels();
        List<LevelResponse> responses = levels.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Read by id
    @GetMapping("/{levelId}")
    @Operation(
        summary = "Get a level by ID", 
        description = "Retrieves a single level's details using its ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved level.", 
                content = @Content(schema = @Schema(implementation = LevelResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Level not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> getLevelById(@PathVariable int levelId) {
        try {
            LevelEntity level = levelServ.getLevelById(levelId);
            LevelResponse response = convertToDto(level);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to find level", e.getMessage()));
        }
    }

    // Update
    @PutMapping("/{levelId}")
    @Operation(
        summary = "Edit an existing level", 
        description = "Allows an admin to modify an existing level's monsters and name.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Level updated successfully.", 
                content = @Content(schema = @Schema(implementation = LevelResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or level edit failed.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Level not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> editLevel(@PathVariable int levelId, @RequestBody LevelEditRequest request) {
        try {
            LevelEntity updatedLevel = levelServ.editLevel(levelId, request);
            LevelResponse response = convertToDto(updatedLevel);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Failed to edit level", e.getMessage()));
        }
    }

    // Delete a LevelEntity by id
    @DeleteMapping("/{levelId}")
    @Operation(
        summary = "Delete an existing level", 
        description = "Allows an admin to permanently delete a level.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Level deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Level not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> deleteLevel(@PathVariable int levelId) {
        try {
            levelServ.deleteLevel(levelId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to delete level", e.getMessage()));
        }
    }

    // Get all completed levels for a user by id
    @GetMapping("/completed/users/{userId}")
    @Operation(
        summary = "Get all completed levels for a user",
        description = "Retrieves a list of all levels completed by a specific user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of completed levels.",
                content = @Content(schema = @Schema(implementation = LevelResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> getCompletedLevelsForUser(@PathVariable int userId) {
        try {
            List<LevelEntity> completedLevels = levelServ.getCompletedLevelsForUser(userId);
            List<CompletedLevelResponse> responses = completedLevels.stream()
                .map(level -> {
                    CompletedLevelResponse completedResponse = new CompletedLevelResponse();
                    completedResponse.setLevelId(level.getLevelId());
                    completedResponse.setLevelName(level.getLevelName());
                    return completedResponse;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to retrieve completed levels", e.getMessage()));
        }
    }

    // Get a list of unique monsters for a level preview
    @GetMapping("/{levelId}/monsters/preview")
    @Operation(
        summary = "Get unique monsters for level preview",
        description = "Retrieves a list of unique monsters, including boss forms, for a level preview. Duplicates are removed.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unique monsters.", 
                content = @Content(schema = @Schema(implementation = MonsterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Level not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> getUniqueMonstersForLevelPreview(@PathVariable int levelId) {
        try {
            Set<MonsterEntity> uniqueMonsters = levelServ.getUniqueMonstersForLevelPreview(levelId);
            List<MonsterResponse> monsterResponses = uniqueMonsters.stream()
                .map(this::convertToMonsterDto)
                .collect(Collectors.toList());
            return ResponseEntity.ok(monsterResponses);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to find level", e.getMessage()));
        }
    }

    // Helper method to convert an entity to a DTO
    private LevelResponse convertToDto(LevelEntity level) {
        LevelResponse dto = new LevelResponse();
        dto.setLevelId(level.getLevelId());
        dto.setLevelName(level.getLevelName());
        dto.setCoinsReward(level.getCoinsReward());
        dto.setGemsReward(level.getGemsReward());
        
        List<LevelMonsterResponse> monsterDtos = level.getLevelMonsters().stream()
            .map(levelMonster -> {
                LevelMonsterResponse monsterDto = new LevelMonsterResponse();
                monsterDto.setId(levelMonster.getId());
                monsterDto.setMonsterType(levelMonster.getMonsterType().name());
                
                // Omitted boss details
                if (levelMonster.getMonster() != null) {
                    MonsterResponse monsterDetails = new MonsterResponse();
                    monsterDetails.setMonsterId(levelMonster.getMonster().getMonsterId());
                    monsterDetails.setTagalogName(levelMonster.getMonster().getTagalogName());
                    monsterDetails.setDescription(levelMonster.getMonster().getDescription());
                    monsterDetails.setImageData(levelMonster.getMonster().getImageData());
                    monsterDto.setMonster(monsterDetails);
                } else {
                    monsterDto.setMonster(null);
                }
                
                // Boss' minion forms
                if (levelMonster.getMonsterType() == MonsterType.BOSS) {
                    List<MonsterResponse> bossForms = levelMonster.getMinionForms().stream()
                        .map(bossForm -> {
                            MonsterResponse formDetails = new MonsterResponse();
                            if (bossForm.getMinionMonster() != null) {
                                if (bossForm.getMinionMonster().getMonster() != null) {
                                    formDetails.setMonsterId(bossForm.getMinionMonster().getMonster().getMonsterId());
                                    formDetails.setTagalogName(bossForm.getMinionMonster().getMonster().getTagalogName());
                                    formDetails.setDescription(bossForm.getMinionMonster().getMonster().getDescription());
                                    formDetails.setImageData(bossForm.getMinionMonster().getMonster().getImageData());
                                }
                            }
                            return formDetails;
                        })
                        .collect(Collectors.toList());
                    monsterDto.setBossForms(bossForms);
                }
                
                return monsterDto;
            })
            .collect(Collectors.toList());
        
        dto.setLevelMonsters(monsterDtos);
        
        return dto;
    }

    private MonsterResponse convertToMonsterDto(MonsterEntity monster) {
        MonsterResponse dto = new MonsterResponse();
        dto.setMonsterId(monster.getMonsterId());
        dto.setTagalogName(monster.getTagalogName());
        dto.setEnglishName(monster.getEnglishName());
        dto.setDescription(monster.getDescription());
        dto.setImageData(monster.getImageData());
        return dto;
    }
    
}
