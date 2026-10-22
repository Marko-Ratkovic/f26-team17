package com.csc340.glowup.dto;

import java.util.List;

public record AdminUserPage(
    List<AdminUserResponse> users,
    int page,
    int size,
    long totalElements,
    int totalPages) {
}
