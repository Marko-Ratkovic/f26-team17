package com.csc340.glowup.entity;

import com.csc340.glowup.enums.TimeBlock;
import com.csc340.glowup.enums.Weekday;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provider_schedule",
        uniqueConstraints = @UniqueConstraint(name = "uk_provider_schedule_day_block",
                columnNames = {"provider_id", "weekday", "time_block"}))
@Getter 
@Setter 
public class ProviderSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "weekday", nullable = false)
    private Weekday weekday;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "time_block", nullable = false)
    private TimeBlock timeBlock;

    @NotNull
    @Column(nullable = false)
    private Boolean isAvailable;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

}
