package com.csc340.glowup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProviderUpdateRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 100) String businessName,
    @Size(max = 2000) String bio,
    @Size(max = 2000) String certifications,
    @Min(0) Integer yearsOfExperience,
    @Size(max = 100) String specialties,
    @NotBlank String addressLine,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipcode
) {

}
