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
        if (providerScheduleRepository.existsByProviderIdAndWeekdayAndTimeBlock(
                providerId, request.weekday(), request.timeBlock())) {
            throw duplicate(request);
        }
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
        if (providerScheduleRepository.existsByProviderIdAndWeekdayAndTimeBlockAndIdNot(
                providerId, request.weekday(), request.timeBlock(), scheduleId)) {
            throw duplicate(request);
        }
        apply(providerSchedule, request);
        return ProviderScheduleResponse.from(providerScheduleRepository.save(providerSchedule));
    }

    private ConflictException duplicate(ProviderScheduleRequest request) {
        return new ConflictException("There is already a " + request.weekday() + " " + request.timeBlock()
                + " entry for this provider; update that one instead.");
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
