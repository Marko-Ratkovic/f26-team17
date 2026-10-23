package com.csc340.glowup.controller;

import com.csc340.glowup.dto.ProviderReviewsResponse;
import com.csc340.glowup.dto.ReplyRequest;
import com.csc340.glowup.dto.ReviewRequest;
import com.csc340.glowup.dto.ReviewResponse;
import com.csc340.glowup.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/customers/{customerId}/appointments/{appointmentId}/review")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long customerId,
                                                 @PathVariable Long appointmentId,
                                                 @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(customerId, appointmentId, request));
    }

    @PutMapping("/customers/{customerId}/reviews/{reviewId}")
    public ReviewResponse edit(@PathVariable Long customerId,
                               @PathVariable Long reviewId,
                               @Valid @RequestBody ReviewRequest request) {
        return reviewService.edit(customerId, reviewId, request);
    }

    @GetMapping("/customers/{customerId}/reviews")
    public List<ReviewResponse> customerReviews(@PathVariable Long customerId) {
        return reviewService.listForCustomer(customerId);
    }

    @GetMapping("/providers/{providerId}/reviews")
    public ProviderReviewsResponse providerReviews(@PathVariable Long providerId) {
        return reviewService.listForProvider(providerId);
    }

    @PostMapping("/providers/{providerId}/reviews/{reviewId}/reply")
    public ResponseEntity<ReviewResponse> reply(@PathVariable Long providerId,
                                                @PathVariable Long reviewId,
                                                @Valid @RequestBody ReplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.reply(providerId, reviewId, request));
    }
}
