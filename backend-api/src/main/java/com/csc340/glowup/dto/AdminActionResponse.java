package com.csc340.glowup.dto;

import com.csc340.glowup.entity.AdminAction;
import com.csc340.glowup.enums.AdminActionType;
import com.csc340.glowup.enums.AdminTargetType;

import java.time.OffsetDateTime;

public record AdminActionResponse(
    Long id,
    AdminActionType action,
    AdminTargetType targetType,
    Long targetId,
    Long adminId,
    String adminEmail,
    String reason,
    OffsetDateTime createdAt) {

    public static AdminActionResponse from(AdminAction adminAction) {
        return new AdminActionResponse(adminAction.getId(), adminAction.getAction(),
                adminAction.getTargetType(), adminAction.getTargetId(),
                adminAction.getAdmin().getId(), adminAction.getAdmin().getEmail(),
                adminAction.getReason(), adminAction.getCreatedAt());
    }
}
