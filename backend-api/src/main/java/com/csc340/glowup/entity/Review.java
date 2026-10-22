package com.csc340.glowup.entity;

import lombok.*;

import java.time.OffsetDateTime;

import com.csc340.glowup.enums.ContentStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    @NotNull 
    private Integer rating;

    @NotNull
    private ContentStatus status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime editdAt;

    @Size(max = 1000)
    @Column(length = 1000)
    private String replyText;

    private ContentStatus replyStatus;

    private OffsetDateTime replyAt;
}
