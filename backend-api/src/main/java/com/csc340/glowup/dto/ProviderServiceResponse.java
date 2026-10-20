package com.csc340.glowup.dto;

import java.math.BigDecimal;

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
    Long providerId) {

    public static ProviderServiceResponse from(ProviderService providerService){
        return new ProviderServiceResponse(providerService.getId(), providerService.getName(), providerService.getDescription(), providerService.getPrice(), 
        providerService.getCategory(),providerService.getDurationMin(), providerService.getStatus(), providerService.getIsVisible(), providerService.getProvider().getId());
    }
        
}
