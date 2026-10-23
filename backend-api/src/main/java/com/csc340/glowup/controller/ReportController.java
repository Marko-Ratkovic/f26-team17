package com.csc340.glowup.controller;

import com.csc340.glowup.dto.ReportRequest;
import com.csc340.glowup.dto.ReviewReportResponse;
import com.csc340.glowup.service.ReviewReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReviewReportService reportService;

    public ReportController(ReviewReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/customers/{customerId}/reports")
    public ResponseEntity<ReviewReportResponse> reportAsCustomer(@PathVariable Long customerId,
                                                                 @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.reportAsCustomer(customerId, request));
    }

    @PostMapping("/providers/{providerId}/reports")
    public ResponseEntity<ReviewReportResponse> reportAsProvider(@PathVariable Long providerId,
                                                                 @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.reportAsProvider(providerId, request));
    }
}
