package com.csc340.glowup.dto;

import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;

import java.time.OffsetDateTime;

public record AdminUserResponse(
    Long id,
    String name,
    String email,
    String phoneNumber,
    AccountRole role,
    AccountStatus status,
    OffsetDateTime createdAt) {

    public static AdminUserResponse from(UserAccount userAccount, String name) {
        return new AdminUserResponse(userAccount.getId(), name, userAccount.getEmail(),
                userAccount.getPhoneNumber(), userAccount.getAccountRole(),
                userAccount.getAccountStatus(), userAccount.getCreatedAt());
    }
}
