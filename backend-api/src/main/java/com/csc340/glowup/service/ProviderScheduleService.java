package com.csc340.glowup.service;

import com.csc340.glowup.entity.ProviderSchedule;

import org.springframework.stereotype.Service;

import com.csc340.glowup.dto.ProviderScheduleRequest;
import com.csc340.glowup.dto.ProviderScheduleResponse;
import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ProviderScheduleRepository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

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

    public ProviderScheduleResponse create(Long providerId, ProviderScheduleRequest request){
        Provider provider = findProvider(providerId);
        ProviderSchedule providerSchedule = new ProviderSchedule();
        providerSchedule.setProvider(provider);
        apply(providerSchedule,request);
        return ProviderScheduleResponse.from(providerScheduleRepository.save(providerSchedule));
    }

    @Transactional(readOnly = true)
    public List<ProviderScheduleResponse> list(Long providerId, boolean availableOnly){
        if(!providerRepository.existsById(providerId)){
            throw new NotFoundException("Provider " + providerId + " was not found.");
        }
        List<ProviderSchedule> providerSchedule = availableOnly
            ? providerScheduleRepository.findAllByProviderIdAndIsAvailableTrue(providerId)
            : providerScheduleRepository.findAllByProviderId(providerId);
        return providerSchedule.stream().map(ProviderScheduleResponse::from).toList();
    }

    public ProviderScheduleResponse update(Long providerId,Long scheduleId, ProviderScheduleRequest request){
        ProviderSchedule providerSchedule = lockSchedule(scheduleId);
        if(!providerSchedule.getProvider().getId().equals(providerId)){
            throw new NotFoundException("Schedule "+scheduleId+" was not found for provider " + providerId);
        }
        apply(providerSchedule, request);
        return ProviderScheduleResponse.from(providerScheduleRepository.save(providerSchedule));
    }

    private Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
            .orElseThrow(() -> new NotFoundException("Provider "+providerId+" was not found."));
    }

    private void apply(ProviderSchedule providerSchedule,ProviderScheduleRequest request){
        providerSchedule.setWeekday(request.weekday());
        providerSchedule.setTimeBlock(request.timeBlock());
        providerSchedule.setIsAvailable(request.isAvailable());
    }

    private ProviderSchedule lockSchedule(Long scheduleId){
        return providerScheduleRepository.findByIdForUpdate(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule "+scheduleId+" was not found."));
    }

}
