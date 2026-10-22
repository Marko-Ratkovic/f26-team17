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
import java.util.Comparator;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provider opening hours: one row per weekday with open and close times (US-5, US-7).
 * A closed day is isAvailable = false and keeps its times, so reopening it does not lose them.
 */
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

    /**
     * Creates all 7 days at registration: the chosen days are open with the given hours,
     * the others are closed but keep the same hours as a starting point.
     */
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
        for(ProviderDailyScheduleUpdate newDailySchedule : request.providerDailySchedule()){
            ProviderSchedule day = providerScheduleRepository.findForUpdateByProviderIdAndWeekday(providerId, newDailySchedule.weekday())
                        .orElseThrow(() -> new IllegalStateException("Schedule not found for weekday: " + newDailySchedule.weekday()));
            if(newDailySchedule.isAvailable()){
                LocalTime open = newDailySchedule.openTime() != null ? newDailySchedule.openTime() : day.getOpenTime();
                LocalTime close = newDailySchedule.closeTime() != null ? newDailySchedule.closeTime() : day.getCloseTime();
                validateHours(true, open, close);
                day.setIsAvailable(true);
                day.setOpenTime(open);
                day.setCloseTime(close);
                
            }
            else {
                day.setIsAvailable(false);
            }
            providerScheduleRepository.save(day);
        }
        List<ProviderSchedule> providerSchedules = providerScheduleRepository.findAllByProviderId(providerId);
        return ProviderUpdateScheduleResponse.from(providerId,providerSchedules);
    }

    /** The same rule for registration and for editing: an open day needs both times, and open before close. */
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

    /** Appointment start times and opening hours use 15-minute steps (:00, :15, :30, :45). */
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
