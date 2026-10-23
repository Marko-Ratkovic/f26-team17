package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record ReviewRequest(
    @NotNull @Min(1) @Max(5) Integer rating,
    @NotBlank @Size(max = 1000) String comment) {
}
