package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Provider;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository 
public interface  ProviderRepository extends JpaRepository<Provider, Long>{
    
    // @Query("""
    //     SELECT DISTINCT p
    //     FROM Provider p
    //     LEFT JOIN p.provider_services ps
    //     WHERE (ps.category = :category AND ps.status = 'APPROVED')
    //     """)
    // List<Provider> findMatchingCategory(@Param("category") ServiceCategory category);
    
    List<Provider> findAllByZipcodeOrderById(String zipcode);

}
