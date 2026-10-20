package com.csc340.glowup.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import com.csc340.glowup.enums.ServiceCategory;

public record ProviderServiceRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 2000) String description,
    @NotNull @DecimalMin("1") @Digits(integer = 8, fraction = 0, message = "must be a whole-dollar amount") BigDecimal price,
    @NotNull ServiceCategory category,
    @NotNull @Min(1) Integer durationMin,
    @Size(max = 255) String imagePath,
    @Size(max = 30) String status, 
    Boolean isVisible){
}
