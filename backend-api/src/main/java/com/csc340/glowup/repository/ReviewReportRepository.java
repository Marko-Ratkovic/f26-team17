package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Review;
import com.csc340.glowup.entity.ReviewReport;
import com.csc340.glowup.enums.AdminTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    boolean existsByReviewIdAndTargetAndReporterIdAndResolvedAtIsNull(
            Long reviewId, AdminTargetType target, Long reporterId);

    List<ReviewReport> findAllByReviewIdOrderByCreatedAtDesc(Long reviewId);

    List<ReviewReport> findAllByReviewIdAndTargetAndResolvedAtIsNull(Long reviewId, AdminTargetType target);

    @Query("SELECT r FROM Review r WHERE EXISTS " +
           "(SELECT rr.id FROM ReviewReport rr WHERE rr.review = r AND rr.resolvedAt IS NULL) ORDER BY r.id")
    List<Review> findReviewsWithOpenReports();

    @Query("SELECT r FROM Review r WHERE r.status = com.csc340.glowup.enums.ContentStatus.HIDDEN " +
           "OR r.replyStatus = com.csc340.glowup.enums.ContentStatus.HIDDEN ORDER BY r.id")
    List<Review> findReviewsWithHiddenContent();
}
