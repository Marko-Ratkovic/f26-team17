package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository 
public interface ProviderScheduleRepository extends JpaRepository<ProviderSchedule, Long> {
    List<ProviderSchedule> findAllProviderIdOrderByStartTime(Long providerId);
    List<ProviderSchedule> findAllProviderIdAndIsAvailableTrueOrderByStartTime(Long providerId);
}
