package com.csc340.glowup.repository;

import com.csc340.glowup.entity.ProviderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AdminServiceRepository extends JpaRepository<ProviderService, Long> {

    @Query("select s.createdAt from ProviderService s where s.createdAt >= :start and s.createdAt < :end")
    List<LocalDateTime> findSubmissionTimesBetween(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);
}
