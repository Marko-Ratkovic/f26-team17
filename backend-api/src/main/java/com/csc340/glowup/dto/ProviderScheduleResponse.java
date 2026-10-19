package com.csc340.glowup.dto;

import java.time.LocalTime;

import com.csc340.glowup.entity.ProviderSchedule;

public record ProviderScheduleResponse(
        Long id,
        LocalTime startTime,
        LocalTime endTime,
        Boolean isAvailable,
        Long providerId) {
    
    public static ProviderScheduleResponse from(ProviderSchedule providerSchedule){
        return new ProviderScheduleResponse(providerSchedule.getId(), providerSchedule.getStartTime(), 
        providerSchedule.getEndTime(), providerSchedule.getIsAvailable(), providerSchedule.getProvider().getId());
    }
}
