package com.csc340.glowup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.csc340.glowup.entity.Appointment;

import java.util.List;

@Repository
public interface ApppointmentRepository extends JpaRepository<Appointment, Long>{
    List<Appointment> findAllByCustomerIdAndProviderId(Long customerId,Long providerId);
}
