package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;

public record ProviderResponse(
    Long id,
    String name,
    String businessName,
    String bio,
    String certifications,
    Integer yearsOfExperience,
    String specialties,
    String imagePath,
    String addressLine,
    String city,
    String zipcode,
    String state) {
    
    public static ProviderResponse from(Provider provider){
        return new ProviderResponse(provider.getId(), provider.getName(), provider.getBusinessName(), provider.getBio(), 
                    provider.getCertifications(), provider.getYearsOfExperience(), provider.getSpecialties(), provider.getImagePath(), 
                    provider.getAddressLine(), provider.getCity(), provider.getZipcode(), provider.getState());
    }
}
