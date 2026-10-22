package com.csc340.glowup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
   List<Appointment> findAllByCustomerIdOrderByIdDesc(Long customerId);

   /** Reschedule requests made from an appointment (rows whose rescheduledFrom is that appointment). */
   List<Appointment> findAllByRescheduledFromIdAndStatus(Long rescheduledFromId, AppointmentStatus status);
   
   @Query("SELECT a FROM Appointment a WHERE a.service.provider.id = :providerId ORDER BY a.id DESC")
   List<Appointment> findAllByProviderService_Provider_IdOrderByIdDesc(@Param("providerId") Long providerId);

   @Query("SELECT a FROM Appointment a WHERE (a.customer.userAccount.id = :userId " +
      "OR a.service.provider.userAccount.id = :userId) " +
      "AND a.status IN (com.csc340.glowup.enums.AppointmentStatus.REQUESTED, com.csc340.glowup.enums.AppointmentStatus.CONFIRMED) " +
      "AND a.startsAt > :now")
   List<Appointment> findUpcomingOpenForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

   @Query("SELECT a FROM Appointment a WHERE a.service.id = :serviceId " +
      "AND a.status = com.csc340.glowup.enums.AppointmentStatus.REQUESTED AND a.startsAt > :now")
   List<Appointment> findUpcomingRequestsForService(@Param("serviceId") Long serviceId, @Param("now") LocalDateTime now);

   /** A provider's CONFIRMED appointments starting in [from, to); the overlap check is done in Java. */
   @Query("SELECT a FROM Appointment a WHERE a.service.provider.id = :providerId " +
      "AND a.status = com.csc340.glowup.enums.AppointmentStatus.CONFIRMED " +
      "AND a.startsAt >= :from AND a.startsAt < :to")
   List<Appointment> findConfirmedForProviderBetween(
            @Param("providerId") Long providerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
