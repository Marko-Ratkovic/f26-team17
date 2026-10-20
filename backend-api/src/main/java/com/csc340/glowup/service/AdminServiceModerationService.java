package com.csc340.glowup.service;

import com.csc340.glowup.dto.AdminActionResponse;
import com.csc340.glowup.dto.AdminServiceDecisionRequest;
import com.csc340.glowup.dto.AdminServicePage;
import com.csc340.glowup.dto.AdminServiceResponse;
import com.csc340.glowup.entity.AdminAction;
import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.AdminActionType;
import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ServiceDecision;
import com.csc340.glowup.repository.AdminActionRepository;
import com.csc340.glowup.repository.AdminServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * US-10: admins review new services before customers see them.
 * Only PENDING services can be decided. Requesting changes or rejecting needs a
 * note for the provider. Every decision is written to the admin audit log in the
 * same transaction; the note is stored there as the reason.
 */
@Service
@Transactional
public class AdminServiceModerationService {

    static final String PENDING = "PENDING";
    static final String APPROVED = "APPROVED";
    static final String CHANGE_REQUESTED = "CHANGE_REQUESTED";
    static final String REJECTED = "REJECTED";
    private static final int MAX_PAGE_SIZE = 100;

    private final AdminAccess adminAccess;
    private final AdminServiceRepository adminServiceRepository;
    private final AdminActionRepository adminActionRepository;

    public AdminServiceModerationService(
        AdminAccess adminAccess,
        AdminServiceRepository adminServiceRepository,
        AdminActionRepository adminActionRepository) {
        this.adminAccess = adminAccess;
        this.adminServiceRepository = adminServiceRepository;
        this.adminActionRepository = adminActionRepository;
    }

    /** The review queue: oldest submissions first so nothing waits forever. */
    @Transactional(readOnly = true)
    public AdminServicePage listServices(Long adminId, String status, String query, int page, int size) {
        adminAccess.requireAdmin(adminId);
        if (page < 0) {
            throw new BadRequestException("page must be 0 or greater.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("size must be between 1 and " + MAX_PAGE_SIZE + ".");
        }
        String statusFilter = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        Page<ProviderService> result = adminServiceRepository.search(statusFilter, SearchPattern.contains(query),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt")));
        List<AdminServiceResponse> services = result.getContent().stream().map(AdminServiceResponse::from).toList();
        return new AdminServicePage(services, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public AdminServiceResponse getService(Long adminId, Long serviceId) {
        adminAccess.requireAdmin(adminId);
        return AdminServiceResponse.from(findService(serviceId));
    }

    @Transactional(readOnly = true)
    public List<AdminActionResponse> getHistory(Long adminId, Long serviceId) {
        adminAccess.requireAdmin(adminId);
        findService(serviceId);
        return adminActionRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtDesc(AdminTargetType.SERVICE, serviceId)
                .stream().map(AdminActionResponse::from).toList();
    }

    public AdminServiceResponse decide(Long adminId, Long serviceId, AdminServiceDecisionRequest request) {
        UserAccount admin = adminAccess.requireAdmin(adminId);
        ProviderService service = findService(serviceId);
        if (!PENDING.equalsIgnoreCase(service.getStatus())) {
            throw new ConflictException("Only services waiting for review can be decided (status: "
                    + service.getStatus() + ").");
        }
        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();
        if (request.decision() != ServiceDecision.APPROVE && note == null) {
            throw new BadRequestException("A note for the provider is required when requesting changes or rejecting.");
        }
        if (request.decision() == ServiceDecision.APPROVE
                && service.getProvider().getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ConflictException("The provider's account is not active, so this service cannot be approved.");
        }

        AdminActionType action = switch (request.decision()) {
            case APPROVE -> {
                service.setStatus(APPROVED);
                service.setIsVisible(true);
                yield AdminActionType.SERVICE_APPROVED;
            }
            case REQUEST_CHANGES -> {
                service.setStatus(CHANGE_REQUESTED);
                service.setIsVisible(false);
                yield AdminActionType.SERVICE_CHANGES_REQUESTED;
            }
            case REJECT -> {
                service.setStatus(REJECTED);
                service.setIsVisible(false);
                yield AdminActionType.SERVICE_REJECTED;
            }
        };
        adminServiceRepository.save(service);
        adminActionRepository.save(AdminAction.of(admin, action, AdminTargetType.SERVICE, serviceId, note));
        return AdminServiceResponse.from(service);
    }

    private ProviderService findService(Long serviceId) {
        return adminServiceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service " + serviceId + " was not found."));
    }
}
