package com.csc340.glowup.service;

import com.csc340.glowup.entity.ProviderSchedule;

import org.springframework.stereotype.Service;

import com.csc340.glowup.dto.ProviderUpdateScheduleRequest;
import com.csc340.glowup.dto.ProviderUpdateScheduleResponse;
import com.csc340.glowup.dto.ProviderDailyScheduleUpdate;
import com.csc340.glowup.dto.ProviderScheduleResponse;
import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.enums.Weekday;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ProviderScheduleRepository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.Comparator;
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

    @Transactional(readOnly = true)
    public List<ProviderScheduleResponse> list(Long providerId, boolean availableOnly){
        if(!providerRepository.existsById(providerId)){
            throw new NotFoundException("Provider " + providerId + " was not found.");
        }
        List<ProviderSchedule> providerSchedule = availableOnly
            ? providerScheduleRepository.findAllByProviderIdAndIsAvailableTrue(providerId)
            : providerScheduleRepository.findAllByProviderId(providerId);
        return providerSchedule.stream()
                .sorted(Comparator.comparing(ProviderSchedule::getWeekday))
                .map(ProviderScheduleResponse::from).toList();
    }

    public void createWeek(Provider provider, Collection<Weekday> openDays, LocalTime openTime, LocalTime closeTime) {
        if (openDays == null || openDays.isEmpty()) {
            throw new BadRequestException("Choose at least one day you are available.");
        }
        validateHours(true, openTime, closeTime);
        for (Weekday weekday : Weekday.values()) {
            ProviderSchedule day = newDay(provider, weekday);
            day.setIsAvailable(openDays.contains(weekday));
            day.setOpenTime(openTime);
            day.setCloseTime(closeTime);
            providerScheduleRepository.save(day);
        }
    }

    public ProviderUpdateScheduleResponse updateWeek(Long providerId, ProviderUpdateScheduleRequest request){
        Provider provider = findProvider(providerId);
        Set<Weekday> seen = EnumSet.noneOf(Weekday.class);
        for(ProviderDailyScheduleUpdate newDailySchedule : request.providerDailySchedule()){
            if(!seen.add(newDailySchedule.weekday())){
                throw new BadRequestException(newDailySchedule.weekday() + " is listed more than once.");
            }
        }
        for(ProviderDailyScheduleUpdate newDailySchedule : request.providerDailySchedule()){
            ProviderSchedule day = providerScheduleRepository.findForUpdateByProviderIdAndWeekday(providerId, newDailySchedule.weekday())
                        .orElseGet(() -> newDay(provider, newDailySchedule.weekday()));
            LocalTime open = newDailySchedule.openTime() != null ? newDailySchedule.openTime() : day.getOpenTime();
            LocalTime close = newDailySchedule.closeTime() != null ? newDailySchedule.closeTime() : day.getCloseTime();
            validateHours(newDailySchedule.isAvailable(), open, close);
            day.setIsAvailable(newDailySchedule.isAvailable());
            day.setOpenTime(open);
            day.setCloseTime(close);
            providerScheduleRepository.save(day);
        }
        List<ProviderSchedule> providerSchedules = providerScheduleRepository.findAllByProviderId(providerId).stream()
                .sorted(Comparator.comparing(ProviderSchedule::getWeekday)).toList();
        return ProviderUpdateScheduleResponse.from(providerId,providerSchedules);
    }

    static void validateHours(Boolean isAvailable, LocalTime openTime, LocalTime closeTime) {
        if (!Boolean.TRUE.equals(isAvailable)) {
            if (openTime != null && closeTime != null && !openTime.isBefore(closeTime)) {
                throw new BadRequestException("Opening time must be before closing time.");
            }
            return;
        }
        if (openTime == null || closeTime == null) {
            throw new BadRequestException("Opening and closing times are required for an open day.");
        }
        if (!openTime.isBefore(closeTime)) {
            throw new BadRequestException("Opening time must be before closing time.");
        }
        if (!isOnQuarterHour(openTime) || !isOnQuarterHour(closeTime)) {
            throw new BadRequestException("Opening and closing times are in 15-minute steps, e.g. 9:00 or 7:30.");
        }
    }

    static boolean isOnQuarterHour(LocalTime time) {
        return time.getMinute() % 15 == 0 && time.getSecond() == 0 && time.getNano() == 0;
    }

    private ProviderSchedule newDay(Provider provider, Weekday weekday) {
        ProviderSchedule day = new ProviderSchedule();
        day.setProvider(provider);
        day.setWeekday(weekday);
        return day;
    }

    private Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
            .orElseThrow(() -> new NotFoundException("Provider "+providerId+" was not found."));
    }
}
