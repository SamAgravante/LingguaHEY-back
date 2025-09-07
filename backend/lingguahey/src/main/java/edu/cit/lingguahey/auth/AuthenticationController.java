package edu.cit.lingguahey.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.cit.lingguahey.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/api/lingguahey/auth")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authService;
    
    private final String frontendBaseUrl;

    public AuthenticationController(AuthenticationService authService,
                                    @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        this.authService = authService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user and returns a JWT token upon successful registration. Email verification may be required.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Registration request body",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully registered and logged in"),
            @ApiResponse(responseCode = "202", description = "Registration accepted, email verification required"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);

        if (response.isEmailVerificationRequired()) {
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            return ResponseEntity.ok(response);
        }
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
            @ApiResponse(responseCode = "403", description = "Forbidden - account not verified or disabled",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse authResponse = authService.authenticate(request);
            return ResponseEntity.ok(authResponse);
        } catch (DisabledException e) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Account Disabled", "Your account is not yet verified. Please check your email for the verification link."));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication Failed", "Incorrect email or password."));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error", "An unexpected error occurred during login."));
        }
    }

    @GetMapping("/verify-email")
    @Operation(
        summary = "Verify user email",
        description = "Verifies a user's email address using a token sent via email and redirects to frontend.",
        responses = {
            @ApiResponse(responseCode = "302", description = "Redirects to frontend with verification status"),
            @ApiResponse(responseCode = "500", description = "Internal server error during verification")
        }
    )
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(frontendBaseUrl).path("verify-status");

        VerificationResult result = authService.verifyEmail(token);

        switch (result) {
            case SUCCESS:
                uriBuilder.queryParam("status", "success");
                uriBuilder.queryParam("message", "Your email has been successfully verified!");
                break;
            case INVALID_TOKEN:
                uriBuilder.queryParam("status", "failure");
                uriBuilder.queryParam("message", "The verification link is invalid or has expired.");
                break;
            case ALREADY_VERIFIED:
                uriBuilder.queryParam("status", "info");
                uriBuilder.queryParam("message", "Your email is already verified. You can now log in.");
                break;
            case NOT_TEACHER_ROLE:
                uriBuilder.queryParam("status", "failure");
                uriBuilder.queryParam("message", "This verification link is not for a teacher account or is invalid.");
                break;
            default:
                uriBuilder.queryParam("status", "failure");
                uriBuilder.queryParam("message", "An unexpected error occurred during verification.");
                break;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(uriBuilder.toUriString()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
