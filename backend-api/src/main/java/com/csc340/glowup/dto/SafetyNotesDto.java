package com.csc340.glowup.dto;

import com.csc340.glowup.entity.CustomerSafetyNotes;
import com.csc340.glowup.enums.AllergyStatus;

public record SafetyNotesDto(
        AllergyStatus allergyStatus, 
        String allergyDetails, 
        String sensitivities) {
    
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
