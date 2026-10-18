package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.security.Provider;
import java.util.List;

@Repository 
public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long>{

    List<ProviderService> findAllByProviderIdOrderById(Long providerId);

    List<ProviderService> findAllByProviderIdAndStatusIgnoreCaseOrderById(Long providerId, String status);

    List<ProviderService> findAllByCategoryIgnoreCaseAndStatusIgnoreCaseOrderById(
                        ServiceCategory category, String status);

    List<ProviderService> findAllByStatusIgnoreCaseOrderById(String status);        
}
