package com.csc340.glowup.service;

import com.csc340.glowup.entity.ProviderSchedule;

import org.springframework.stereotype.Service;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ProviderScheduleRepository;

import jakarta.transaction.Transactional;

@Service 
@Transactional 
public class ProviderScheduleService {

    private final ProviderScheduleRepository providerScheduleRepository;
    private final ProviderRepository providerRepository;

    public ProviderScheduleService(
            ProviderScheduleRepository providerScheduleRepository,
            ProviderRepository providerRepository){
        this.providerScheduleRepository = providerScheduleRepository;
        this.providerRepository = providerRepository;
    }
    
}
