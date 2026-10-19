package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.enums.ServiceCategory;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository 
public interface  ProviderRepository extends JpaRepository<Provider, Long>{
    
    // @Query("""
    //         select distinct provider
    //         from Provider provider
    //         left join provider.providerService providerService
    //         where (lower(providerService.category) = lower(:category)
    //                 and upper(providerService.status) = 'APPROVED')
    //         """)
    // List<Provider> findMatchingCategory(@Param("category") ServiceCategory category);

}
