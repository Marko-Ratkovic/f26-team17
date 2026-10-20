package com.csc340.glowup.enums;

/**
 * Review state of a service (SRS US-6, US-10). Only admins move a service out of PENDING.
 * Customers only ever see APPROVED services from ACTIVE providers.
 */
public enum ServiceStatus {
    PENDING,
    CHANGES_REQUESTED,
    APPROVED,
    REJECTED,
    ARCHIVED
}
