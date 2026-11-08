package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record CustomerRegisterRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters long") String password,
    @Size(max = 20) @Pattern(regexp = "^$|^(\\+?1[ -]?)?(\\(\\d{3}\\)|\\d{3})[ -]?\\d{3}[ -]?\\d{4}$", message = "Enter a 10-digit phone number, like 336-555-0101") String phoneNumber) {
}
