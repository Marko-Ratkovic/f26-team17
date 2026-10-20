package com.csc340.glowup.dto;

import com.csc340.glowup.entity.ProviderSchedule;
import com.csc340.glowup.enums.TimeBlock;
import com.csc340.glowup.enums.Weekday;

public record ProviderScheduleResponse(
        Long id,
        Weekday weekday,
        TimeBlock timeBlock,
        Boolean isAvailable,
        Long providerId) {
    
    public static ProviderScheduleResponse from(ProviderSchedule providerSchedule){
        return new ProviderScheduleResponse(
            providerSchedule.getId(), providerSchedule.getWeekday(),
            providerSchedule.getTimeBlock(), providerSchedule.getIsAvailable(), providerSchedule.getProvider().getId());
    }
}
