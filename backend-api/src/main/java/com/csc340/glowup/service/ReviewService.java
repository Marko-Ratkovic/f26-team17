package com.csc340.glowup.service;

import com.csc340.glowup.dto.ProviderReviewsResponse;
import com.csc340.glowup.dto.ReplyRequest;
import com.csc340.glowup.dto.ReviewRequest;
import com.csc340.glowup.dto.ReviewResponse;
import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.entity.Customer;
import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.entity.Review;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.ContentStatus;
import com.csc340.glowup.repository.AppointmentRepository;
import com.csc340.glowup.repository.CustomerRepository;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    static final int REVIEW_WINDOW_DAYS = 30;

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         AppointmentRepository appointmentRepository,
                         CustomerRepository customerRepository,
                         ProviderRepository providerRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
    }

    public ReviewResponse create(Long customerId, Long appointmentId, ReviewRequest request) {
        requireActive(findCustomer(customerId));
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .filter(found -> found.getCustomer().getId().equals(customerId))
                .orElseThrow(() -> new NotFoundException("Appointment " + appointmentId + " was not found."));
        if (!appointment.isCompleted()) {
            throw new ConflictException("Only completed appointments can be reviewed.");
        }
        if (appointment.getEndsAt().isBefore(LocalDateTime.now(Appointment.TIME_ZONE).minusDays(REVIEW_WINDOW_DAYS))) {
            throw new ConflictException("Reviews can be written up to " + REVIEW_WINDOW_DAYS
                    + " days after the appointment.");
        }
        if (reviewRepository.existsByAppointmentId(appointmentId)) {
            throw new ConflictException("This appointment has already been reviewed.");
        }
        Review review = new Review();
        review.setAppointment(appointment);
        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public ReviewResponse edit(Long customerId, Long reviewId, ReviewRequest request) {
        requireActive(findCustomer(customerId));
        Review review = findForCustomer(customerId, reviewId);
        if (review.getStatus() == ContentStatus.HIDDEN) {
            throw new ConflictException("A hidden review cannot be edited.");
        }
        if (review.getReplyText() != null) {
            throw new ConflictException("A review cannot be edited after the provider replies.");
        }
        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        review.setEditedAt(OffsetDateTime.now());
        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listForCustomer(Long customerId) {
        findCustomer(customerId);
        return reviewRepository.findAllForCustomer(customerId).stream().map(ReviewResponse::from).toList();
    }

    // public reviews page: only active providers and visible reviews
    @Transactional(readOnly = true)
    public ProviderReviewsResponse listForProvider(Long providerId) {
        Provider provider = findProvider(providerId);
        if (provider.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new NotFoundException("Provider " + providerId + " was not found.");
        }
        Double average = null;
        long count = 0;
        var rating = reviewRepository.findRatingForProvider(providerId);
        if (rating.isPresent()) {
            average = roundRating(rating.get().getAverageRating());
            count = rating.get().getReviewCount();
        }
        List<ReviewResponse> reviews = reviewRepository.findVisibleForProvider(providerId)
                .stream().map(ReviewResponse::from).toList();
        return new ProviderReviewsResponse(provider.getId(), provider.getName(), average, count, reviews);
    }

    public ReviewResponse reply(Long providerId, Long reviewId, ReplyRequest request) {
        Provider provider = findProvider(providerId);
        if (provider.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenException("Your account is not active.");
        }
        Review review = reviewRepository.findById(reviewId)
                .filter(found -> found.getAppointment().getProvider().getId().equals(providerId))
                .orElseThrow(() -> new NotFoundException("Review " + reviewId + " was not found."));
        if (review.getStatus() == ContentStatus.HIDDEN) {
            throw new ConflictException("You cannot reply to a hidden review.");
        }
        if (review.getReplyText() != null) {
            throw new ConflictException("You have already replied to this review.");
        }
        review.setReplyText(request.text().trim());
        review.setReplyStatus(ContentStatus.VISIBLE);
        review.setRepliedAt(OffsetDateTime.now());
        return ReviewResponse.from(review);
    }

    public static Double roundRating(Double average) {
        return average == null ? null : Math.round(average * 10) / 10.0;
    }

    private Review findForCustomer(Long customerId, Long reviewId) {
        return reviewRepository.findById(reviewId)
                .filter(found -> found.getAppointment().getCustomer().getId().equals(customerId))
                .orElseThrow(() -> new NotFoundException("Review " + reviewId + " was not found."));
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " was not found."));
    }

    private Provider findProvider(Long providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider " + providerId + " was not found."));
    }

    private static void requireActive(Customer customer) {
        if (customer.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenException("Your account is not active.");
        }
    }
}
