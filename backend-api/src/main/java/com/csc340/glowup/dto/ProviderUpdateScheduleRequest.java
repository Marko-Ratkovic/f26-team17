package com.csc340.glowup.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record ProviderUpdateScheduleRequest(
    @NotEmpty @Size(max = 7) List<@Valid @NotNull ProviderDailyScheduleUpdate> providerDailySchedule){
}


