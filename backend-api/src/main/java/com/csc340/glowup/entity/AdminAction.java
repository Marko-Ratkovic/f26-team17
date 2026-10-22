package com.csc340.glowup.entity;

import com.csc340.glowup.enums.AdminActionType;
import com.csc340.glowup.enums.AdminTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "admin_actions",
        indexes = @Index(name = "idx_admin_actions_target", columnList = "target_type, target_id"))
@Getter
@Setter
@NoArgsConstructor
public class AdminAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private UserAccount admin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AdminActionType action;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private AdminTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Size(max = 1000)
    @Column(length = 1000)
    private String reason;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public static AdminAction of(UserAccount admin, AdminActionType action,
                                 AdminTargetType targetType, Long targetId, String reason) {
        AdminAction adminAction = new AdminAction();
        adminAction.setAdmin(admin);
        adminAction.setAction(action);
        adminAction.setTargetType(targetType);
        adminAction.setTargetId(targetId);
        adminAction.setReason(reason);
        return adminAction;
    }
}
