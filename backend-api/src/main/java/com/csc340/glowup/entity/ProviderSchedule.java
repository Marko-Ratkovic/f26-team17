package com.csc340.glowup.entity;

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

import java.time.LocalTime;

@Entity
@Table(name = "provider_schedule",
        uniqueConstraints = @UniqueConstraint(name = "uk_provider_schedule_day",
                columnNames = {"provider_id", "weekday"}))
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

    @NotNull
    @Column(nullable = false)
    private Boolean isAvailable;

    private LocalTime openTime;

    private LocalTime closeTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    public boolean covers(LocalTime start, LocalTime end) {
        return Boolean.TRUE.equals(isAvailable) && openTime != null && closeTime != null
                && !start.isBefore(openTime) && !end.isAfter(closeTime);
    }

    public boolean overlaps(LocalTime from, LocalTime to) {
        return Boolean.TRUE.equals(isAvailable) && openTime != null && closeTime != null
                && openTime.isBefore(to) && closeTime.isAfter(from);
    }
}
