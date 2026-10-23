package com.csc340.glowup.repository;

import com.csc340.glowup.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    interface RatingSummary {
        Long getProviderId();
        Double getAverageRating();
        Long getReviewCount();
    }

    boolean existsByAppointmentId(Long appointmentId);

    @Query("SELECT r FROM Review r WHERE r.appointment.customer.id = :customerId ORDER BY r.createdAt DESC")
    List<Review> findAllForCustomer(@Param("customerId") Long customerId);

    @Query("SELECT r FROM Review r WHERE r.appointment.service.provider.id = :providerId " +
           "AND r.status = com.csc340.glowup.enums.ContentStatus.VISIBLE ORDER BY r.createdAt DESC")
    List<Review> findVisibleForProvider(@Param("providerId") Long providerId);

    @Query("SELECT p.id AS providerId, AVG(r.rating) AS averageRating, COUNT(r) AS reviewCount " +
           "FROM Review r JOIN r.appointment a JOIN a.service s JOIN s.provider p " +
           "WHERE r.status = com.csc340.glowup.enums.ContentStatus.VISIBLE AND p.id = :providerId GROUP BY p.id")
    Optional<RatingSummary> findRatingForProvider(@Param("providerId") Long providerId);

    @Query("SELECT p.id AS providerId, AVG(r.rating) AS averageRating, COUNT(r) AS reviewCount " +
           "FROM Review r JOIN r.appointment a JOIN a.service s JOIN s.provider p " +
           "WHERE r.status = com.csc340.glowup.enums.ContentStatus.VISIBLE GROUP BY p.id")
    List<RatingSummary> findRatingsForAllProviders();
}
