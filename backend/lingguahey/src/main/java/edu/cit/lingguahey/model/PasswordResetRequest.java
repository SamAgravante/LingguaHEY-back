package edu.cit.lingguahey.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Schema(description = "Current (old) password", required = true)
    private String oldPassword;
    @Schema(description = "Desired new password", required = true)
    private String newPassword;
}
