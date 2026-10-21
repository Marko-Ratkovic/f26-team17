package com.csc340.glowup.enums;

/**
 * COMPLETED is set when the provider marks it (only after it ends). A CONFIRMED appointment whose end
 * time has passed also counts as completed (Appointment.isCompleted()), so reviews and stats never
 * depend on the provider remembering to click.
 */
public enum AppointmentStatus {
    REQUESTED,
    CONFIRMED,
    DECLINED,
    CANCELLED,
    COMPLETED,
    RESCHEDULED
}
