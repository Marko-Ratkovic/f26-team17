package com.csc340.glowup.dto;

import java.util.List;

import com.csc340.glowup.entity.ProviderSchedule;

public record ProviderUpdateScheduleResponse(
        Long providerId,
        List<ProviderDailyScheduleUpdate> providerDailySchedule) {
    
    public static ProviderUpdateScheduleResponse from(Long providerId, List<ProviderSchedule> schedules){
        List<ProviderDailyScheduleUpdate> dailyUpdates = schedules.stream()
            .map(schedule -> new ProviderDailyScheduleUpdate(
                schedule.getWeekday(),
                schedule.getIsAvailable(),
                schedule.getOpenTime(),
                schedule.getCloseTime()
            )).toList();
        return new ProviderUpdateScheduleResponse(providerId, dailyUpdates);
    }
}
