package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record ProviderRegistrationRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String businessName,
        @Size(max = 2000) String bio,
        @Size(max = 2000) String certifications,
        @NotNull @Min(0) Integer yearsOfExperience,
        @Size(max = 1000) String specialties,
        String imagePath,
        @NotBlank String addressLine,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zipcode
        ) {
    
}
