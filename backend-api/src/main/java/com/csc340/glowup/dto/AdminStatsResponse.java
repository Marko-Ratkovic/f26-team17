package com.csc340.glowup.dto;

import java.time.LocalDate;
import java.util.List;


public record AdminStatsResponse(
    LocalDate from,
    LocalDate to,
    String timeZone,
    Long completedAppointments,
    long newUsers,
    long newCustomers,
    long newProviders,
    long activeProviders,
    long pendingProviderApprovals,
    long servicesSubmitted,
    List<AdminMonthlyStats> months) {
}
