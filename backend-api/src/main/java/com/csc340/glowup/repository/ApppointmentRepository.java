package com.csc340.glowup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.enums.TimeBlock;
import com.csc340.glowup.enums.Weekday;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApppointmentRepository extends JpaRepository<Appointment, Long>{
   List<Appointment> findAllByCustomerIdOrderByIdDesc(Long customerId);
   
   @Query("SELECT a FROM Appointment a WHERE a.service.provider.id = :providerId ORDER BY a.id DESC")
   List<Appointment> findAllByProviderService_Provider_IdOrderByIdDesc(@Param("providerId") Long providerId);

   @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.service.provider.id = :providerId " +
      "AND a.status != 'CANCELLED' " +
      "AND (:startDateTime < FUNCTION('TIMESTAMPADD', MINUTE, a.service.durationMin, a.startsAt) " +
      "AND :endDateTime > a.startsAt)")
   boolean existsConflictingAppointment(
            @Param("providerId") Long providerId, 
            @Param("startDateTime") LocalDateTime startDateTime, 
            @Param("endDateTime") LocalDateTime endDateTime);
}
