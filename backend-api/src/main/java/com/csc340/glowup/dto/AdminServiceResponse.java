package com.csc340.glowup.dto;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;
import com.csc340.glowup.enums.ServiceStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AdminServiceResponse(
    Long id,
    String name,
    ServiceCategory category,
    String description,
    BigDecimal price,
    Integer durationMin,
    String imagePath,
    ServiceStatus status,
    Boolean isVisible,
    Long providerId,
    String providerName,
    Long parentServiceId,
    String reviewNote,
    OffsetDateTime submittedAt,
    OffsetDateTime updatedAt) {

    public static AdminServiceResponse from(ProviderService service) {
        return new AdminServiceResponse(service.getId(), service.getName(), service.getCategory(),
                service.getDescription(), service.getPrice(), service.getDurationMin(), service.getImagePath(),
                service.getStatus(), service.getIsVisible(), service.getProvider().getId(),
                service.getProvider().getName(),
                service.getParentService() == null ? null : service.getParentService().getId(),
                service.getReviewNote(),
                service.getCreatedAt(), service.getUpdatedAt());
    }
}
