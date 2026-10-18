package com.csc340.glowup.service;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.repository.ProviderRepository;
import org.springframework.transaction.annotation.Transactional;
import com.csc340.glowup.dto.ProviderResponse;
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

}
