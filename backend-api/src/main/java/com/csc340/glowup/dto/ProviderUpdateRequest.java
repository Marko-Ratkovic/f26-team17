package com.csc340.glowup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProviderUpdateRequest(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 20) @Pattern(regexp = "^$|^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number format") String phoneNumber,
    @Size(max = 200) String businessName,
    @Size(max = 2000) String bio,
    @Size(max = 2000) String certifications,
    @NotNull @Min(0) Integer yearsOfExperience,
    @NotBlank String addressLine,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipcode
) {

}
