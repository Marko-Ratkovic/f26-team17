package com.csc340.glowup.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;
import com.csc340.glowup.enums.ServiceStatus;

public record ProviderServiceResponse(
    Long id,
    String name, 
    String description,
    BigDecimal price,
    ServiceCategory category,
    Integer durationMin,
    ServiceStatus status,
    Boolean isVisible,
    LocalDate createdAt,
    LocalDate updatedAt,
    Long providerId,
    Long parentServiceId,
    String reviewNote) {

    public static ProviderServiceResponse from(ProviderService providerService){
        return new ProviderServiceResponse(providerService.getId(), providerService.getName(), providerService.getDescription(), providerService.getPrice(), 
        providerService.getCategory(),providerService.getDurationMin(), providerService.getStatus(), providerService.getIsVisible(), providerService.getCreatedAt().toLocalDate(), providerService.getUpdatedAt().toLocalDate(), providerService.getProvider().getId(),
        providerService.getParentService() == null ? null : providerService.getParentService().getId(),
        providerService.getReviewNote());
    }
        
}
