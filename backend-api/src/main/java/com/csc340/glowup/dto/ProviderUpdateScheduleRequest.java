package com.csc340.glowup.dto;

import java.util.List;

import jakarta.validation.constraints.*;

public record ProviderUpdateScheduleRequest(
    @NotNull List<ProviderDailyScheduleUpdate> providerDailySchedule){
}


