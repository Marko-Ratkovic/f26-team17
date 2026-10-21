package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderSchedule;
import com.csc340.glowup.enums.Weekday;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface ProviderScheduleRepository extends JpaRepository<ProviderSchedule, Long> {

    List<ProviderSchedule> findAllByProviderIdAndIsAvailableTrue(Long providerId);

    List<ProviderSchedule> findAllByProviderId(Long providerId);

    Optional<ProviderSchedule> findByProviderIdAndWeekday(Long providerId, Weekday weekday);

    /** Locks the day's row while it is edited, so two edits at once cannot both create it. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProviderSchedule> findForUpdateByProviderIdAndWeekday(Long providerId, Weekday weekday);
}
