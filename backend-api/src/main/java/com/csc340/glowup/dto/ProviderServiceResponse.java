package com.csc340.glowup.dto;

import java.math.BigDecimal;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;
import com.csc340.glowup.enums.ServiceStatus;

/**
 * parentServiceId: set on a draft edit; the id of the live version it will replace.
 * reviewNote: the admin's reason when changes were requested or the service was rejected.
 */
public record ProviderServiceResponse(
    Long id,
    String name, 
    String description,
    BigDecimal price,
    ServiceCategory category,
    Integer durationMin,
    ServiceStatus status,
    Boolean isVisible,
    Long providerId,
    Long parentServiceId,
    String reviewNote) {

    public static ProviderServiceResponse from(ProviderService providerService){
        return new ProviderServiceResponse(providerService.getId(), providerService.getName(), providerService.getDescription(), providerService.getPrice(), 
        providerService.getCategory(),providerService.getDurationMin(), providerService.getStatus(), providerService.getIsVisible(), providerService.getProvider().getId(),
        providerService.getParentService() == null ? null : providerService.getParentService().getId(),
        providerService.getReviewNote());
    }
        
}
