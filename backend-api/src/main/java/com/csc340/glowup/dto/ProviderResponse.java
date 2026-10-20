package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;

public record ProviderResponse(
    Long id,
    String name,
    String bio,
    String certifications,
    Integer yearsOfExperience,
    String imagePath,
    String addressLine,
    String city,
    String zipcode,
    String state) {
    
    public static ProviderResponse from(Provider provider){
        return new ProviderResponse(provider.getId(), provider.getName(), provider.getBio(), 
                    provider.getCertifications(), provider.getYearsOfExperience(), provider.getImagePath(), 
                    provider.getAddressLine(), provider.getCity(), provider.getZipcode(), provider.getState());
    }
}
