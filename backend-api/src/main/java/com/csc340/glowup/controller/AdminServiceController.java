package com.csc340.glowup.controller;

import com.csc340.glowup.dto.AdminActionResponse;
import com.csc340.glowup.dto.AdminDecisionRequest;
import com.csc340.glowup.dto.AdminServiceDecisionRequest;
import com.csc340.glowup.dto.AdminServicePage;
import com.csc340.glowup.dto.AdminServiceResponse;
import com.csc340.glowup.enums.ServiceStatus;
import com.csc340.glowup.service.AdminServiceModerationService;
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
@RequestMapping("/api/admin/services")
public class AdminServiceController {

    private final AdminServiceModerationService moderationService;

    public AdminServiceController(AdminServiceModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping
    public AdminServicePage list(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @RequestParam(required = false) ServiceStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return moderationService.listServices(adminId, status, q, page, size);
    }

    @GetMapping("/{serviceId}")
    public AdminServiceResponse get(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long serviceId) {
        return moderationService.getService(adminId, serviceId);
    }

    @GetMapping("/{serviceId}/actions")
    public List<AdminActionResponse> history(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long serviceId) {
        return moderationService.getHistory(adminId, serviceId);
    }

    @PostMapping("/{serviceId}/archive")
    public AdminServiceResponse archive(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long serviceId,
            @Valid @RequestBody AdminDecisionRequest request) {
        return moderationService.archive(adminId, serviceId, request.reason());
    }

    @PostMapping("/{serviceId}/decision")
    public AdminServiceResponse decide(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long serviceId,
            @Valid @RequestBody AdminServiceDecisionRequest request) {
        return moderationService.decide(adminId, serviceId, request);
    }
}
