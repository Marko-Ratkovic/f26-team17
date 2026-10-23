package com.csc340.glowup.dto;

import java.util.List;

public record ProviderReviewsResponse(
    Long providerId,
    String providerName,
    Double averageRating,
    long reviewCount,
    List<ReviewResponse> reviews) {
}
