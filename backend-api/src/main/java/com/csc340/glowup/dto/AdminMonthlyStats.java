package com.csc340.glowup.dto;

public record AdminMonthlyStats(
    String month,
    Long completedAppointments,
    long newUsers,
    long servicesSubmitted) {
}
