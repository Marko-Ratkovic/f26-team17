package com.csc340.glowup.entity;

import lombok.*;

import java.time.OffsetDateTime;

import com.csc340.glowup.enums.ContentStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Min(1)
    @Max(5)
    @NotNull
    @Column(nullable = false)
    private Integer rating;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String comment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentStatus status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime editedAt;

    @Size(max = 1000)
    @Column(length = 1000)
    private String replyText;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ContentStatus replyStatus;

    private OffsetDateTime repliedAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
        if (status == null) {
            status = ContentStatus.VISIBLE;
        }
    }
}
