package com.csc340.glowup.dto;

import java.util.List;

/** One page of the admin service review list. page starts at 0. */
public record AdminServicePage(
    List<AdminServiceResponse> services,
    int page,
    int size,
    long totalElements,
    int totalPages) {
}
