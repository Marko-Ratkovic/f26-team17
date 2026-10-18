package com.csc340.glowup.service;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.repository.ProviderRepository;
import org.springframework.transaction.annotation.Transactional;
import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.dto.ProviderUpdateRequest;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProviderProfileService {
    
    private ProviderRepository providerRepository;

    public ProviderProfileService(ProviderRepository providerRepository){
        this.providerRepository = providerRepository;
    }

    public List<Provider> findAll(){
        return providerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProviderResponse getProfile(Long providerId){
        return ProviderResponse.from(findProvider(providerId));
    }

    Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider " + providerId + " was not found."));
    }

    public ProviderResponse updateProfile(Long provideId, ProviderUpdateRequest request){
        Provider provider = findProvider(provideId);
        provider.setName(request.name().trim());
        provider.setBusinessName(request.businessName().translateEscapes());
        provider.setBio(request.bio());
        provider.setCertifications(request.certifications());
        provider.setYearsOfExperience(request.yearsOfExperience());
        provider.setSpecialties(request.specialties());
        provider.setAddressLine(request.addressLine());        
        provider.setCity(request.city());    
        provider.setState(request.state());    
        provider.setZipcode(request.zipcode());    
        return ProviderResponse.from(providerRepository.save(provider));
    }

}
