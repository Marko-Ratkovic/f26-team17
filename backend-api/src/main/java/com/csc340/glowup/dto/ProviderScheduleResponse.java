package com.csc340.glowup.dto;

import com.csc340.glowup.entity.ProviderSchedule;
import com.csc340.glowup.enums.Weekday;

import java.time.LocalTime;

public record ProviderScheduleResponse(
        Long id,
        Weekday weekday,
        Boolean isAvailable,
        LocalTime openTime,
        LocalTime closeTime,
        Long providerId) {
    
    public static ProviderScheduleResponse from(ProviderSchedule providerSchedule){
        return new ProviderScheduleResponse(
            providerSchedule.getId(), providerSchedule.getWeekday(), providerSchedule.getIsAvailable(),
            providerSchedule.getOpenTime(), providerSchedule.getCloseTime(), providerSchedule.getProvider().getId());
    }
}
