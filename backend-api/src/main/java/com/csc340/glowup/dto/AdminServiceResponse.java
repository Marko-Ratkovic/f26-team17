package com.csc340.glowup.dto;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** A service as the admin reviews it, with who submitted it and when. */
public record AdminServiceResponse(
    Long id,
    String name,
    ServiceCategory category,
    String description,
    BigDecimal price,
    Integer durationMin,
    String imagePath,
    String status,
    Boolean isVisible,
    Long providerId,
    String providerName,
    LocalDateTime submittedAt,
    LocalDateTime updatedAt) {

    public static AdminServiceResponse from(ProviderService service) {
        return new AdminServiceResponse(service.getId(), service.getName(), service.getCategory(),
                service.getDescription(), service.getPrice(), service.getDurationMin(), service.getImagePath(),
                service.getStatus(), service.getIsVisible(), service.getProvider().getId(),
                service.getProvider().getName(), service.getCreatedAt(), service.getUpdatedAt());
    }
}
