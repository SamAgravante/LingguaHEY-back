package edu.cit.lingguahey.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response object for API errors.")
public class ErrorResponse {

    @Schema(description = "A general description of the error.")
    private String error;
    
    @Schema(description = "A specific, detailed error message.")
    private String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
