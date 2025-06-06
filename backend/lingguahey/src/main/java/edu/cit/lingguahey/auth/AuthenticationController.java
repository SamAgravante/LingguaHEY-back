package edu.cit.lingguahey.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lingguahey/auth")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user and returns a JWT token upon successful registration",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Registration request body",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login a user",
        description = "Logs in a user and returns a JWT token upon successful authentication",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Authentication request body",
            required = true,
            content = @Content(schema = @Schema(implementation = AuthenticationRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
