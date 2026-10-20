package com.csc340.glowup.dto;

/** One row of the monthly table. month is "YYYY-MM". completedAppointments is null until appointments exist. */
public record AdminMonthlyStats(
    String month,
    Long completedAppointments,
    long newUsers,
    long servicesSubmitted) {
}
