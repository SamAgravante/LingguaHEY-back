package edu.cit.lingguahey.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.lingguahey.Entity.MonsterEntity;
import edu.cit.lingguahey.Service.MonsterService;
import edu.cit.lingguahey.model.ErrorResponse;
import edu.cit.lingguahey.model.MonsterCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/lingguahey/monsters")
@Tag(name = "Monsters")
public class MonsterController {

    @Autowired
    private MonsterService monsterServ;

    // Create
    @PostMapping(value = "", consumes = "multipart/form-data")
    @Operation(
        summary = "Create a new monster", 
        description = "Allows an admin to add a new enemy to the database with a binary image upload.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Monster created successfully.", 
                content = @Content(schema = @Schema(implementation = MonsterEntity.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or monster already exists.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> createMonster(
        @RequestParam("tagalogName") String tagalogName,
        @RequestParam("englishName") String englishName,
        @RequestParam("description") String description,
        @RequestParam("file") MultipartFile file
    ) {
        try {
            MonsterCreateRequest request = new MonsterCreateRequest();
            request.setTagalogName(tagalogName);
            request.setEnglishName(englishName);
            request.setDescription(description);
            request.setImageData(file.getBytes());
            
            MonsterEntity newMonster = monsterServ.createMonster(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newMonster);
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Failed to create monster", e.getMessage()));
        }
    }

    // Read all
    @GetMapping("")
    @Operation(
        summary = "Get all monsters", 
        description = "Retrieves a list of all existing monsters.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of monsters.", 
                content = @Content(schema = @Schema(implementation = MonsterEntity.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<List<MonsterEntity>> getAllMonsters() {
        List<MonsterEntity> monsters = monsterServ.getAllMonsters();
        return ResponseEntity.ok(monsters);
    }

    // Read by id
    @GetMapping("/{monsterId}")
    @Operation(
        summary = "Get a monster by ID", 
        description = "Retrieves a single monster's details using its ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved monster.", 
                content = @Content(schema = @Schema(implementation = MonsterEntity.class))
            ),
            @ApiResponse(responseCode = "404", description = "Monster not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> getMonsterById(@PathVariable int monsterId) {
        try {
            MonsterEntity monster = monsterServ.getMonsterById(monsterId);
            return ResponseEntity.ok(monster);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to find monster", e.getMessage()));
        }
    }

    // Update
    @PutMapping(value = "/{monsterId}", consumes = "multipart/form-data")
    @Operation(
        summary = "Edit an existing monster", 
        description = "Allows an admin to modify the details of an existing enemy.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Monster updated successfully.", 
                content = @Content(schema = @Schema(implementation = MonsterEntity.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or monster with the same name already exists.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Monster not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> editMonster(
        @PathVariable int monsterId,
        @RequestParam("tagalogName") String tagalogName,
        @RequestParam("englishName") String englishName,
        @RequestParam("description") String description,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            MonsterCreateRequest request = new MonsterCreateRequest();
            request.setTagalogName(tagalogName);
            request.setEnglishName(englishName);
            request.setDescription(description);

            if (file != null && !file.isEmpty()) {
                request.setImageData(file.getBytes());
            }

            MonsterEntity updatedMonster = monsterServ.editMonster(monsterId, request);
            return ResponseEntity.ok(updatedMonster);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("File upload failed", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to edit monster", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Failed to edit monster", e.getMessage()));
        }
    }

    // Delete a MonsterEntity by id
    @DeleteMapping("/{monsterId}")
    @Operation(
        summary = "Delete an existing monster", 
        description = "Allows an admin to permanently delete a monster.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Monster deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Monster not found.", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<?> deleteMonster(@PathVariable int monsterId) {
        try {
            monsterServ.deleteMonster(monsterId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Failed to delete monster", e.getMessage()));
        }
    }

}
