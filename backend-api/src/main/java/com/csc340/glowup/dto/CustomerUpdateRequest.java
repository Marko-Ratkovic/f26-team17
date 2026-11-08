package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record CustomerUpdateRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @Size(max = 20) @Pattern(regexp = "^$|^(\\+?1[ -]?)?(\\(\\d{3}\\)|\\d{3})[ -]?\\d{3}[ -]?\\d{4}$", message = "Enter a 10-digit phone number, like 336-555-0101") String phoneNumber) {
}
