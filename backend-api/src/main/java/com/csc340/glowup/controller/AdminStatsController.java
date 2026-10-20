package com.csc340.glowup.controller;

import com.csc340.glowup.dto.AdminStatsResponse;
import com.csc340.glowup.service.AdminStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Admin usage statistics (US-12).
 * Example: GET /api/admin/stats?from=2026-04-01&to=2026-09-30  (dates are optional)
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping
    public AdminStatsResponse stats(
            @RequestHeader(value = AdminUserController.ADMIN_HEADER, required = false) Long adminId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return adminStatsService.getStats(adminId, from, to);
    }
}
