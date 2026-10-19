package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record CustomerUpdateRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @Size(max = 20) @Pattern(regexp = "^$|^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number format") String phoneNumber) {
}
