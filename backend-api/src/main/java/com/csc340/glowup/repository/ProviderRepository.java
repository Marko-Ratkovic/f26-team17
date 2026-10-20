package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Provider;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository 
public interface  ProviderRepository extends JpaRepository<Provider, Long>{
    
    
    List<Provider> findAllByZipcodeOrderById(String zipcode);

}
