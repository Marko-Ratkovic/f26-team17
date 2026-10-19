package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record ProviderRegistrationRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 255) String password,
        @Size(max = 20) String phoneNumber,
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
