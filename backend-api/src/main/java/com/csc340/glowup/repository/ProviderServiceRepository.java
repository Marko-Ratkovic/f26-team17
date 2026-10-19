package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository 
public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long>{

    List<ProviderService> findAllByProviderIdOrderById(Long providerId);

    List<ProviderService> findAllByProviderIdAndStatusIgnoreCaseAndIsVisibleOrderById(Long providerId, String status,Boolean isVisible);

    List<ProviderService> findAllByCategoryIgnoreCaseAndStatusIgnoreCaseAndIsVisibleOrderById(
                        ServiceCategory category, String status, Boolean isVisible);

    List<ProviderService> findAllByStatusIgnoreCaseAndIsVisibleOrderById(String status,Boolean isVisible);        
}
