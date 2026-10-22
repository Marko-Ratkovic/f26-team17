package com.csc340.glowup.controller;

import com.csc340.glowup.dto.AdminActionResponse;
import com.csc340.glowup.dto.AdminDecisionRequest;
import com.csc340.glowup.dto.AdminUserPage;
import com.csc340.glowup.dto.AdminUserResponse;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.service.AdminUserService;
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
@RequestMapping("/api/admin/users")
public class AdminUserController {

    static final String ADMIN_HEADER = "X-Admin-Id";

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public AdminUserPage list(
            @RequestHeader(value = ADMIN_HEADER, required = false) Long adminId,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) AccountRole role,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return adminUserService.listUsers(adminId, status, role, q, page, size);
    }

    @GetMapping("/{userId}")
    public AdminUserResponse get(
            @RequestHeader(value = ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long userId) {
        return adminUserService.getUser(adminId, userId);
    }

    @GetMapping("/{userId}/actions")
    public List<AdminActionResponse> history(
            @RequestHeader(value = ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long userId) {
        return adminUserService.getHistory(adminId, userId);
    }

    @PostMapping("/{userId}/approve")
    public AdminUserResponse approve(
            @RequestHeader(value = ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request) {
        return adminUserService.approveProvider(adminId, userId, request.reason());
    }

    @PostMapping("/{userId}/suspend")
    public AdminUserResponse suspend(
            @RequestHeader(value = ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request) {
        return adminUserService.suspend(adminId, userId, request.reason());
    }

    @PostMapping("/{userId}/reinstate")
    public AdminUserResponse reinstate(
            @RequestHeader(value = ADMIN_HEADER, required = false) Long adminId,
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request) {
        return adminUserService.reinstate(adminId, userId, request.reason());
    }
}
