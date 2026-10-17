package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Provider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  ProviderRepository extends JpaRepository<Provider, Long>{
    
}
