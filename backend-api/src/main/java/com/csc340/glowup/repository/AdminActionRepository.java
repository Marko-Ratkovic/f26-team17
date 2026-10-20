package com.csc340.glowup.repository;

import com.csc340.glowup.entity.AdminAction;
import com.csc340.glowup.enums.AdminActionType;
import com.csc340.glowup.enums.AdminTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, Long> {

    List<AdminAction> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(AdminTargetType targetType, Long targetId);

    boolean existsByTargetTypeAndTargetIdAndAction(AdminTargetType targetType, Long targetId, AdminActionType action);
}
