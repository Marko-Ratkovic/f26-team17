package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** Admin-only appointment queries (usage statistics), kept apart from the customer/provider appointment code. */
@Repository
public interface AdminAppointmentRepository extends JpaRepository<Appointment, Long> {

    /** Start time and duration of every appointment with one of these statuses starting in [from, to). */
    @Query("""
            select a.startsAt, coalesce(a.durationMin, s.durationMin) from Appointment a join a.service s
            where a.status in :statuses and a.startsAt >= :from and a.startsAt < :to
            """)
    List<Object[]> findStartsAndDurations(@Param("statuses") Collection<AppointmentStatus> statuses,
                                          @Param("from") LocalDateTime from,
                                          @Param("to") LocalDateTime to);
}
