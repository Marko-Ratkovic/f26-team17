package com.csc340.glowup.controller;

import com.csc340.glowup.dto.AdminReviewResponse;
import com.csc340.glowup.dto.ModerationRequest;
import com.csc340.glowup.service.AdminReviewModerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final AdminReviewModerationService moderationService;

    public AdminReviewController(AdminReviewModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping
    public List<AdminReviewResponse> list(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @RequestParam(defaultValue = "false") boolean hiddenOnly) {
        return moderationService.list(adminId, hiddenOnly);
    }

    @GetMapping("/{reviewId}")
    public AdminReviewResponse get(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long reviewId) {
        return moderationService.get(adminId, reviewId);
    }

    @PostMapping("/{reviewId}/decision")
    public AdminReviewResponse decide(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ModerationRequest request) {
        return moderationService.decide(adminId, reviewId, request);
    }
}
