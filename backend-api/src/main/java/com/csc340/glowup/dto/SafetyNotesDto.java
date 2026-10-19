package com.csc340.glowup.dto;

import com.csc340.glowup.entity.CustomerSafetyNotes;
import com.csc340.glowup.enums.AllergyStatus;
import jakarta.validation.constraints.*;

public record SafetyNotesDto(
        @NotNull AllergyStatus allergyStatus, 
        @Size(max = 500) String allergyDetails, 
        @Size(max = 500) String sensitivities) {
    
    public static SafetyNotesDto from(CustomerSafetyNotes safetyNotes) {
        if (safetyNotes == null) {
            return null;
        }
        return new SafetyNotesDto(
                safetyNotes.getAllergyStatus(),
                safetyNotes.getAllergyDetails(),
                safetyNotes.getSensitivities());
    }
}
