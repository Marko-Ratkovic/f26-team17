package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;


@Repository
public interface AdminServiceRepository extends JpaRepository<ProviderService, Long> {

    /**
     * status must be upper-case, or "" for any status.
     * pattern comes from SearchPattern.contains(...).
     */
    @Query("""
            select s from ProviderService s join s.provider p
            where (:status = '' or upper(s.status) = :status)
              and (lower(s.name) like :pattern escape '\\' or lower(p.name) like :pattern escape '\\')
            """)
    Page<ProviderService> search(@Param("status") String status,
                                 @Param("pattern") String pattern,
                                 Pageable pageable);

    @Query("select s.createdAt from ProviderService s where s.createdAt >= :start and s.createdAt < :end")
    List<OffsetDateTime> findSubmissionTimesBetween(@Param("start") OffsetDateTime start,
                                                    @Param("end") OffsetDateTime end);
}
