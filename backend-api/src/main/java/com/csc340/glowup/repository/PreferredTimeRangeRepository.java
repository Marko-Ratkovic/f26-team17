package com.csc340.glowup.repository;

import com.csc340.glowup.entity.PreferredTimeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferredTimeRangeRepository extends JpaRepository<PreferredTimeRange, Long> {
    List<PreferredTimeRange> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long customerId);
}
