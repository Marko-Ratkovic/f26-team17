package com.csc340.glowup.entity;

import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor  
public class Appointment {

    /** startsAt is a local time at the provider's business; all GlowUp providers are in this zone. */
    public static final ZoneId TIME_ZONE = ZoneId.of("America/New_York");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false) 
    private ProviderService service;

    @NotNull
    @Column(nullable = false) 
    private LocalDateTime startsAt;

    /**
     * Duration and price saved when the appointment is requested, so a later edit of the service
     * never changes an existing booking. Null only on rows made before these columns existed.
     */
    private Integer durationMin;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime requestedAt;

    private OffsetDateTime decidedAt;

    private OffsetDateTime cancelledAt;

    /** Who cancelled: CUSTOMER or PROVIDER (null unless CANCELLED). */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AccountRole cancelledBy;

    private OffsetDateTime completedAt;

    @Size(max = 500)
    @Column(length = 500)
    private String statusReason;

    // set only on the new REQUESTED row created by a reschedule request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescheduled_from_id")
    private Appointment rescheduledFrom;

    @PrePersist
    void onCreate() {
        if (requestedAt == null) {
            requestedAt = OffsetDateTime.now();
        }
    }

    // not a column: JPA maps the fields above, so these two are plain convenience methods
    public LocalDateTime getEndsAt() {
        return startsAt.plusMinutes(getBookedDurationMin());
    }

    /** The duration saved at booking; falls back to the service for rows made before it was saved. */
    public int getBookedDurationMin() {
        return durationMin != null ? durationMin : service.getDurationMin();
    }

    /** The price saved at booking; falls back to the service for rows made before it was saved. */
    public BigDecimal getBookedPrice() {
        return price != null ? price : service.getPrice();
    }

    public Provider getProvider() {
        return service.getProvider();
    }

    public boolean hasEnded() {
        return getEndsAt().isBefore(LocalDateTime.now(TIME_ZONE));
    }

    /** A request nobody answered before its start time. Worked out from the time, not stored. */
    public boolean isExpired() {
        return status == AppointmentStatus.REQUESTED && !startsAt.isAfter(LocalDateTime.now(TIME_ZONE));
    }

    /**
     * Completed = marked COMPLETED by the provider, or CONFIRMED and already over.
     * Reviews (US-4) and admin stats (US-12) use this, so they never wait on the provider's click.
     */
    public boolean isCompleted() {
        return status == AppointmentStatus.COMPLETED
                || (status == AppointmentStatus.CONFIRMED && hasEnded());
    }
}
