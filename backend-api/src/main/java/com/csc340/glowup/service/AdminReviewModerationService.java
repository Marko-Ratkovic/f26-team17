package com.csc340.glowup.service;

import com.csc340.glowup.dto.AdminReviewResponse;
import com.csc340.glowup.dto.ModerationRequest;
import com.csc340.glowup.entity.AdminAction;
import com.csc340.glowup.entity.Review;
import com.csc340.glowup.entity.ReviewReport;
import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AdminActionType;
import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ContentStatus;
import com.csc340.glowup.repository.AdminActionRepository;
import com.csc340.glowup.repository.ReviewReportRepository;
import com.csc340.glowup.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AdminReviewModerationService {

    private final AdminAccess adminAccess;
    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reportRepository;
    private final AdminActionRepository adminActionRepository;

    public AdminReviewModerationService(AdminAccess adminAccess,
                                        ReviewRepository reviewRepository,
                                        ReviewReportRepository reportRepository,
                                        AdminActionRepository adminActionRepository) {
        this.adminAccess = adminAccess;
        this.reviewRepository = reviewRepository;
        this.reportRepository = reportRepository;
        this.adminActionRepository = adminActionRepository;
    }

    // default: reviews with open reports; hiddenOnly: reviews whose review or reply is hidden (to restore)
    @Transactional(readOnly = true)
    public List<AdminReviewResponse> list(Long adminId, boolean hiddenOnly) {
        adminAccess.requireAdmin(adminId);
        List<Review> reviews = hiddenOnly
                ? reportRepository.findReviewsWithHiddenContent()
                : reportRepository.findReviewsWithOpenReports();
        return reviews.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AdminReviewResponse get(Long adminId, Long reviewId) {
        adminAccess.requireAdmin(adminId);
        return toResponse(findReview(reviewId));
    }

    // keep or hide resolves every open report on that review or reply
    public AdminReviewResponse decide(Long adminId, Long reviewId, ModerationRequest request) {
        UserAccount admin = adminAccess.requireAdmin(adminId);
        Review review = findReview(reviewId);
        AdminTargetType target = ReviewReportService.checkTarget(request.target());
        if (target == AdminTargetType.REPLY && review.getReplyText() == null) {
            throw new NotFoundException("Review " + reviewId + " has no reply.");
        }
        ContentStatus current = target == AdminTargetType.REVIEW ? review.getStatus() : review.getReplyStatus();
        List<ReviewReport> openReports =
                reportRepository.findAllByReviewIdAndTargetAndResolvedAtIsNull(reviewId, target);

        AdminActionType action = switch (request.decision()) {
            case KEEP_VISIBLE -> {
                if (current != ContentStatus.VISIBLE) {
                    throw new ConflictException("Hidden content cannot be kept visible; restore it instead.");
                }
                if (openReports.isEmpty()) {
                    throw new ConflictException("There are no open reports to resolve.");
                }
                yield AdminActionType.CONTENT_KEPT_VISIBLE;
            }
            case HIDE -> {
                if (current == ContentStatus.HIDDEN) {
                    throw new ConflictException("This content is already hidden.");
                }
                setStatus(review, target, ContentStatus.HIDDEN);
                yield AdminActionType.CONTENT_HIDDEN;
            }
            case RESTORE -> {
                if (current != ContentStatus.HIDDEN) {
                    throw new ConflictException("Only hidden content can be restored.");
                }
                setStatus(review, target, ContentStatus.VISIBLE);
                yield AdminActionType.CONTENT_RESTORED;
            }
        };

        ContentStatus outcome = target == AdminTargetType.REVIEW ? review.getStatus() : review.getReplyStatus();
        OffsetDateTime now = OffsetDateTime.now();
        for (ReviewReport report : openReports) {
            report.setResolvedAt(now);
            report.setResolvedBy(admin);
            report.setOutcome(outcome);
        }
        adminActionRepository.save(AdminAction.of(admin, action, target, reviewId, request.reason().trim()));
        return toResponse(review);
    }

    private static void setStatus(Review review, AdminTargetType target, ContentStatus status) {
        if (target == AdminTargetType.REVIEW) {
            review.setStatus(status);
        } else {
            review.setReplyStatus(status);
        }
    }

    private AdminReviewResponse toResponse(Review review) {
        List<AdminAction> history = new ArrayList<>();
        history.addAll(adminActionRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtDesc(AdminTargetType.REVIEW, review.getId()));
        history.addAll(adminActionRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtDesc(AdminTargetType.REPLY, review.getId()));
        history.sort(Comparator.comparing(AdminAction::getCreatedAt).reversed());
        return AdminReviewResponse.from(review,
                reportRepository.findAllByReviewIdOrderByCreatedAtDesc(review.getId()), history);
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review " + reviewId + " was not found."));
    }
}
