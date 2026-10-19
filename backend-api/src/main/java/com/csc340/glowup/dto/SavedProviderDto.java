package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;

public record SavedProviderDto(
        Long id,
        String businessName) {
    
    public static SavedProviderDto from(Provider provider) {
        return new SavedProviderDto(
                provider.getId(),
                provider.getBusinessName());
    }
}
