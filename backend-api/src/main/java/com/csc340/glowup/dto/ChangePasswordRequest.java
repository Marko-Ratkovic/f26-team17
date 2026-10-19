package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record ChangePasswordRequest(
    @NotBlank String currentPassword,
    @NotBlank @Size(min = 8, max = 72, message = "New password must be 8 to 72 characters long") String newPassword) {
}
