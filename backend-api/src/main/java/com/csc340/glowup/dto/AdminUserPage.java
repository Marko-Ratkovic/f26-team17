package com.csc340.glowup.dto;

import java.util.List;

/** One page of the admin user list. page starts at 0. */
public record AdminUserPage(
    List<AdminUserResponse> users,
    int page,
    int size,
    long totalElements,
    int totalPages) {
}
