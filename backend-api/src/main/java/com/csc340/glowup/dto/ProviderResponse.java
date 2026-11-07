package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.enums.AccountStatus;

public record ProviderResponse(
    Long id,
    String name,
    String email,
    String phone,
    String bio,
    String certifications,
    Integer yearsOfExperience,
    String imagePath,
    String addressLine,
    String city,
    String zipcode,
    String state,
    AccountStatus accountStatus) {
    
    public static ProviderResponse from(Provider provider){
        return new ProviderResponse(provider.getId(), provider.getName(), provider.getUserAccount().getEmail(), provider.getUserAccount().getPhoneNumber(),
                    provider.getBio(), provider.getCertifications(), provider.getYearsOfExperience(), provider.getImagePath(), 
                    provider.getAddressLine(), provider.getCity(), provider.getZipcode(), provider.getState(),provider.getUserAccount().getAccountStatus());
    }
}
