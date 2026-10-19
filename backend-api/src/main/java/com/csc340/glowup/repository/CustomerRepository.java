package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
}
