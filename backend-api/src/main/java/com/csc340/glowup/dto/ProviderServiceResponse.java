package com.csc340.glowup.dto;

import java.math.BigDecimal;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;

public record ProviderServiceResponse(
    Long id,
    String name, 
    String description,
    BigDecimal price,
    ServiceCategory category,
    String status,
    Long providerId) {

    public static ProviderServiceResponse from(ProviderService providerService){
        return new ProviderServiceResponse(providerService.getId(), providerService.getName(), providerService.getDescription(), providerService.getPrice(), 
        providerService.getCategory(), providerService.getStatus(), providerService.getProvider().getId());
    }
        
}
