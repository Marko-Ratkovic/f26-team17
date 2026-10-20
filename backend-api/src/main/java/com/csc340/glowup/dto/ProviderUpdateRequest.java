package com.csc340.glowup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProviderUpdateRequest(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 2000) String bio,
    @Size(max = 2000) String certifications,
    @NotNull @Min(0) Integer yearsOfExperience,
    @NotBlank String addressLine,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipcode
) {

}
