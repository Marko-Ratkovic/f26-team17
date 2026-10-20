package com.csc340.glowup.service;

import com.csc340.glowup.dto.AdminActionResponse;
import com.csc340.glowup.dto.AdminUserPage;
import com.csc340.glowup.dto.AdminUserResponse;
import com.csc340.glowup.entity.AdminAction;
import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.AdminActionType;
import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.repository.AdminActionRepository;
import com.csc340.glowup.repository.AdminUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * US-9: admins approve pending providers and suspend or reinstate accounts.
 * Every change takes effect immediately and is written to the admin audit log
 * in the same transaction, so a status change is never saved without its log row.
 */
@Service
@Transactional
public class AdminUserService {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminUserRepository adminUserRepository;
    private final AdminActionRepository adminActionRepository;
    private final AdminAccess adminAccess;

    public AdminUserService(
        AdminUserRepository adminUserRepository,
        AdminActionRepository adminActionRepository,
        AdminAccess adminAccess) {
        this.adminUserRepository = adminUserRepository;
        this.adminActionRepository = adminActionRepository;
        this.adminAccess = adminAccess;
    }

    @Transactional(readOnly = true)
    public AdminUserPage listUsers(Long adminId, AccountStatus status, AccountRole role,
                                   String query, int page, int size) {
        adminAccess.requireAdmin(adminId);
        if (page < 0) {
            throw new BadRequestException("page must be 0 or greater.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("size must be between 1 and " + MAX_PAGE_SIZE + ".");
        }
        String search = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        Page<UserAccount> result = adminUserRepository.search(status, role, "%" + search + "%",
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<AdminUserResponse> users = result.getContent().stream().map(this::toResponse).toList();
        return new AdminUserPage(users, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUser(Long adminId, Long userId) {
        adminAccess.requireAdmin(adminId);
        return toResponse(findUser(userId));
    }

    @Transactional(readOnly = true)
    public List<AdminActionResponse> getHistory(Long adminId, Long userId) {
        adminAccess.requireAdmin(adminId);
        findUser(userId);
        return adminActionRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtDesc(AdminTargetType.USER, userId)
                .stream().map(AdminActionResponse::from).toList();
    }

    /** PENDING_APPROVAL provider → ACTIVE. Only providers wait for approval. */
    public AdminUserResponse approveProvider(Long adminId, Long userId, String reason) {
        UserAccount admin = adminAccess.requireAdmin(adminId);
        UserAccount user = findUser(userId);
        if (user.getAccountRole() != AccountRole.PROVIDER) {
            throw new BadRequestException("Only provider accounts need approval.");
        }
        if (user.getAccountStatus() != AccountStatus.PENDING_APPROVAL) {
            throw new ConflictException("Account " + userId + " is not waiting for approval (status: "
                    + user.getAccountStatus() + ").");
        }
        return changeStatus(admin, user, AccountStatus.ACTIVE, AdminActionType.PROVIDER_APPROVED, reason);
    }

    /** ACTIVE or PENDING_APPROVAL → SUSPENDED. Closed accounts cannot be suspended. */
    public AdminUserResponse suspend(Long adminId, Long userId, String reason) {
        UserAccount admin = adminAccess.requireAdmin(adminId);
        UserAccount user = findUser(userId);
        if (admin.getId().equals(user.getId())) {
            throw new BadRequestException("You cannot suspend your own account.");
        }
        AccountStatus status = user.getAccountStatus();
        if (status != AccountStatus.ACTIVE && status != AccountStatus.PENDING_APPROVAL) {
            throw new ConflictException("Only active or pending accounts can be suspended (status: "
                    + status + ").");
        }
        return changeStatus(admin, user, AccountStatus.SUSPENDED, AdminActionType.ACCOUNT_SUSPENDED, reason);
    }

    /**
     * SUSPENDED → ACTIVE. A provider who was suspended before ever being approved
     * goes back to PENDING_APPROVAL, so reinstating never skips the approval step.
     */
    public AdminUserResponse reinstate(Long adminId, Long userId, String reason) {
        UserAccount admin = adminAccess.requireAdmin(adminId);
        UserAccount user = findUser(userId);
        if (user.getAccountStatus() != AccountStatus.SUSPENDED) {
            throw new ConflictException("Only suspended accounts can be reinstated (status: "
                    + user.getAccountStatus() + ").");
        }
        boolean neverApproved = user.getAccountRole() == AccountRole.PROVIDER
                && !adminActionRepository.existsByTargetTypeAndTargetIdAndAction(
                        AdminTargetType.USER, userId, AdminActionType.PROVIDER_APPROVED);
        AccountStatus newStatus = neverApproved ? AccountStatus.PENDING_APPROVAL : AccountStatus.ACTIVE;
        return changeStatus(admin, user, newStatus, AdminActionType.ACCOUNT_REINSTATED, reason);
    }

    private AdminUserResponse changeStatus(UserAccount admin, UserAccount user, AccountStatus newStatus,
                                           AdminActionType action, String reason) {
        user.setAccountStatus(newStatus);
        adminUserRepository.save(user);
        adminActionRepository.save(AdminAction.of(admin, action, AdminTargetType.USER, user.getId(), reason.trim()));
        return toResponse(user);
    }

    private UserAccount findUser(Long userId) {
        return adminUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account " + userId + " was not found."));
    }

    private AdminUserResponse toResponse(UserAccount user) {
        String name = user.getProvider() != null
                ? user.getProvider().getName()
                : adminUserRepository.findCustomerName(user.getId()).orElse(null);
        return AdminUserResponse.from(user, name);
    }
}
