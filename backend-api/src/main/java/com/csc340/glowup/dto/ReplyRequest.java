package com.csc340.glowup.dto;

import jakarta.validation.constraints.*;

public record ReplyRequest(
    @NotBlank @Size(max = 1000) String text) {
}
