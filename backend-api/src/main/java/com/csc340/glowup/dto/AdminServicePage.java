package com.csc340.glowup.dto;

import java.util.List;

public record AdminServicePage(
    List<AdminServiceResponse> services,
    int page,
    int size,
    long totalElements,
    int totalPages) {
}
