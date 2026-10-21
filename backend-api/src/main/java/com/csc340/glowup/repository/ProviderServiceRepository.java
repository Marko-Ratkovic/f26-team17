package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.ServiceCategory;
import com.csc340.glowup.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long>{

    List<ProviderService> findAllByProviderIdOrderById(Long providerId);

    @Query("SELECT COUNT(ps) > 0 FROM ProviderService ps WHERE ps.parentService.id = :parentServiceId AND ps.status = :status")
    Boolean existsByParentServiceIdAndStatus(@Param("parentServiceId") Long parentServiceId, @Param("status") ServiceStatus status);

    default List<ProviderService> findPublic(ServiceCategory category, Long providerId) {
        return findPublic(ServiceStatus.APPROVED, AccountStatus.ACTIVE, category, providerId);
    }

    @Query("""
            select s from ProviderService s
            where s.status = :status and s.isVisible = true
              and s.provider.userAccount.accountStatus = :accountStatus
              and (:category is null or s.category = :category)
              and (:providerId is null or s.provider.id = :providerId)
            order by s.id
            """)
    List<ProviderService> findPublic(@Param("status") ServiceStatus status,
                                     @Param("accountStatus") AccountStatus accountStatus,
                                     @Param("category") ServiceCategory category,
                                     @Param("providerId") Long providerId);
}
