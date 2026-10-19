package com.csc340.glowup.entity;

import com.csc340.glowup.enums.AllergyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "customer_safety_notes")
@Getter
@Setter
@NoArgsConstructor 
public class CustomerSafetyNotes {
    
    @Id
    private Long customerId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllergyStatus allergyStatus;

    @Size(max = 500)
    @Column(length = 500)
    private String allergyDetails;

    @Size(max = 500)
    @Column(length = 500)
    private String sensitivities;

    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = OffsetDateTime.now();
    }
}
