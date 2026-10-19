package com.csc340.glowup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.csc340.glowup.entity.UserAccount;

@Repository 
public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
    boolean existsByEmailIgnoreCase(String email);
}
