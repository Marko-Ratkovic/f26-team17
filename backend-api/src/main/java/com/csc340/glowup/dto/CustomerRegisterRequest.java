package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record CustomerRegisterRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters long") String password,
    @Size(max = 20) @Pattern(regexp = "^$|^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number format") String phoneNumber) {
}
