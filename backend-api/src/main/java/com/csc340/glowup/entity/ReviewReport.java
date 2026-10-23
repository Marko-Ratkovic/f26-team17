package com.csc340.glowup.entity;

import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ContentStatus;
import com.csc340.glowup.enums.ReportReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;

// target is REVIEW or REPLY; a reply has no id of its own, so both point at the review
@Entity
@Table(name = "review_reports",
        indexes = @Index(name = "idx_review_reports_review", columnList = "review_id, target"))
@Getter
@Setter
@NoArgsConstructor
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdminTargetType target;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserAccount reporter;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportReason reason;

    @Size(max = 500)
    @Column(length = 500)
    private String details;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // null while the report is open
    private OffsetDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private UserAccount resolvedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ContentStatus outcome;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public boolean isOpen() {
        return resolvedAt == null;
    }
}
