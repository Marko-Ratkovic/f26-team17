package com.csc340.glowup.service;

import com.csc340.glowup.dto.ReportRequest;
import com.csc340.glowup.dto.ReviewReportResponse;
import com.csc340.glowup.entity.Customer;
import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.entity.Review;
import com.csc340.glowup.entity.ReviewReport;
import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ContentStatus;
import com.csc340.glowup.repository.CustomerRepository;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ReviewReportRepository;
import com.csc340.glowup.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewReportService {

    private final ReviewReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;

    public ReviewReportService(ReviewReportRepository reportRepository,
                               ReviewRepository reviewRepository,
                               CustomerRepository customerRepository,
                               ProviderRepository providerRepository) {
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
    }

    // a customer can report any review or reply except their own
    public ReviewReportResponse reportAsCustomer(Long customerId, ReportRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " was not found."));
        Review review = findReview(request.reviewId());
        return save(customer.getUserAccount(), review, targetOf(request), request);
    }

    // a provider can report reviews of their own services
    public ReviewReportResponse reportAsProvider(Long providerId, ReportRequest request) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider " + providerId + " was not found."));
        if (targetOf(request) != AdminTargetType.REVIEW) {
            throw new BadRequestException("Providers can report reviews of their own services only.");
        }
        Review review = findReview(request.reviewId());
        if (!review.getAppointment().getProvider().getId().equals(providerId)) {
            throw new NotFoundException("Review " + request.reviewId() + " was not found.");
        }
        return save(provider.getUserAccount(), review, AdminTargetType.REVIEW, request);
    }

    private ReviewReportResponse save(UserAccount reporter, Review review, AdminTargetType target,
                                      ReportRequest request) {
        if (reporter.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenException("Your account is not active.");
        }
        UserAccount author;
        ContentStatus current;
        if (target == AdminTargetType.REVIEW) {
            author = review.getAppointment().getCustomer().getUserAccount();
            current = review.getStatus();
        } else {
            if (review.getReplyText() == null) {
                throw new NotFoundException("Review " + review.getId() + " has no reply.");
            }
            author = review.getAppointment().getProvider().getUserAccount();
            current = review.getReplyStatus();
        }
        if (author.getId().equals(reporter.getId())) {
            throw new BadRequestException("You cannot report your own content.");
        }
        if (current == ContentStatus.HIDDEN) {
            throw new ConflictException("This content is already hidden.");
        }
        if (reportRepository.existsByReviewIdAndTargetAndReporterIdAndResolvedAtIsNull(
                review.getId(), target, reporter.getId())) {
            throw new ConflictException("You already have an open report on this content.");
        }
        ReviewReport report = new ReviewReport();
        report.setReview(review);
        report.setTarget(target);
        report.setReporter(reporter);
        report.setReason(request.reason());
        report.setDetails(request.details() == null || request.details().isBlank() ? null : request.details().trim());
        return ReviewReportResponse.from(reportRepository.save(report));
    }

    static AdminTargetType targetOf(ReportRequest request) {
        return checkTarget(request.target());
    }

    static AdminTargetType checkTarget(AdminTargetType target) {
        if (target == null) {
            return AdminTargetType.REVIEW;
        }
        if (target != AdminTargetType.REVIEW && target != AdminTargetType.REPLY) {
            throw new BadRequestException("target must be REVIEW or REPLY.");
        }
        return target;
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review " + reviewId + " was not found."));
    }
}
