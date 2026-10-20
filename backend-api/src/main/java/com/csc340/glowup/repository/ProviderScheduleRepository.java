package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderSchedule;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface ProviderScheduleRepository extends JpaRepository<ProviderSchedule, Long> {
    List<ProviderSchedule> findAllByProviderIdAndWeekdayAndTimeBlockOrderByWeekday(Long providerId, String weekday, String timeBlock);
    List<ProviderSchedule> findAllByProviderIdAndIsAvailableTrue(Long providerId);
    List<ProviderSchedule> findAllByProviderId(Long providerId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select providerSchedule from ProviderSchedule providerSchedule where providerSchedule.id = :id")
    Optional<ProviderSchedule> findByIdForUpdate(@Param("id") Long id);
}
