package com.csc340.glowup.repository;

import com.csc340.glowup.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    
    boolean existsByEmail(String email);
}
