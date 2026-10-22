package com.csc340.glowup.service;

import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.repository.UserAccountRepository;
import org.springframework.stereotype.Component;

/** Temporary until login: the caller sends their account id, and it must be an ACTIVE admin. */
@Component
public class AdminAccess {

    private final UserAccountRepository userAccountRepository;

    public AdminAccess(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount requireAdmin(Long adminId) {
        if (adminId == null) {
            throw new ForbiddenException("Admin access required.");
        }
        UserAccount admin = userAccountRepository.findById(adminId)
                .orElseThrow(() -> new ForbiddenException("Admin access required."));
        if (admin.getAccountRole() != AccountRole.ADMIN || admin.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenException("Admin access required.");
        }
        return admin;
    }
}
