package com.csc340.glowup.repository;

import com.csc340.glowup.entity.CustomerSafetyNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface CustomerSafetyNotesRepository extends JpaRepository<CustomerSafetyNotes, Long> {
}
