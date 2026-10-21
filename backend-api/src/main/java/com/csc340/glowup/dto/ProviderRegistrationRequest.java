package com.csc340.glowup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import com.csc340.glowup.enums.Weekday;

import java.time.LocalTime;
import java.util.List;

public record ProviderRegistrationRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters long") String password,
        @Size(max = 20) String phoneNumber,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 2000) String bio,
        @Size(max = 2000) String certifications,
        @NotNull @Min(0) Integer yearsOfExperience,
        String imagePath,
        @NotBlank String addressLine,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zipcode,
        // starting opening hours (US-5): these days are open from openTime to closeTime, the others closed;
        // each day can be changed later with PUT /api/providers/{id}/schedules/{weekday}
        @NotEmpty List<Weekday> availableDays,
        @NotNull LocalTime openTime,
        @NotNull LocalTime closeTime
        ) {
    
}
