package com.csc340.glowup.dto;

/** One row of the monthly table. month is "YYYY-MM". An appointment counts in the month it ended. */
public record AdminMonthlyStats(
    String month,
    Long completedAppointments,
    long newUsers,
    long servicesSubmitted) {
}
