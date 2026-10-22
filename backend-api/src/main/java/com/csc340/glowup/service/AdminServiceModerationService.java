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
import com.csc340.glowup.enums.ServiceStatus;
import com.csc340.glowup.repository.AdminActionRepository;
import com.csc340.glowup.repository.AdminServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminServiceModerationService {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminAccess adminAccess;
    private final AdminServiceRepository adminServiceRepository;
    private final AdminActionRepository adminActionRepository;
    private final ServiceCatalogService serviceCatalogService;

    public AdminServiceModerationService(
        AdminAccess adminAccess,
        AdminServiceRepository adminServiceRepository,
        AdminActionRepository adminActionRepository,
        ServiceCatalogService serviceCatalogService) {
        this.adminAccess = adminAccess;
        this.adminServiceRepository = adminServiceRepository;
        this.adminActionRepository = adminActionRepository;
        this.serviceCatalogService = serviceCatalogService;
    }

    @Transactional(readOnly = true)
    public AdminServicePage listServices(Long adminId, ServiceStatus status, String query, int page, int size) {
        adminAccess.requireAdmin(adminId);
        if (page < 0) {
            throw new BadRequestException("page must be 0 or greater.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("size must be between 1 and " + MAX_PAGE_SIZE + ".");
        }
        Page<ProviderService> result = adminServiceRepository.search(status, SearchPattern.contains(query),
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
        if (service.getStatus() != ServiceStatus.PENDING) {
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
        if (request.decision() == ServiceDecision.APPROVE && service.getParentService() != null) {
            return approveDraft(admin, service, note);
        }

        AdminActionType action = switch (request.decision()) {
            case APPROVE -> {
                service.setStatus(ServiceStatus.APPROVED);
                service.setIsVisible(true);
                yield AdminActionType.SERVICE_APPROVED;
            }
            case REQUEST_CHANGES -> {
                service.setStatus(ServiceStatus.CHANGES_REQUESTED);
                service.setIsVisible(false);
                yield AdminActionType.SERVICE_CHANGES_REQUESTED;
            }
            case REJECT -> {
                service.setStatus(ServiceStatus.REJECTED);
                service.setIsVisible(false);
                yield AdminActionType.SERVICE_REJECTED;
            }
        };
        service.setReviewNote(note);
        adminServiceRepository.save(service);
        adminActionRepository.save(AdminAction.of(admin, action, AdminTargetType.SERVICE, serviceId, note));
        return AdminServiceResponse.from(service);
    }

    public AdminServiceResponse archive(Long adminId, Long serviceId, String reason) {
        UserAccount admin = adminAccess.requireAdmin(adminId);
        ProviderService service = findService(serviceId);
        if (service.getStatus() != ServiceStatus.APPROVED) {
            throw new ConflictException("Only live (approved) services can be taken down (status: "
                    + service.getStatus() + ").");
        }
        String note = reason.trim();
        service.setReviewNote(note);
        serviceCatalogService.archive(service);
        adminActionRepository.save(AdminAction.of(admin, AdminActionType.SERVICE_ARCHIVED,
                AdminTargetType.SERVICE, serviceId, note));
        return AdminServiceResponse.from(service);
    }

    private AdminServiceResponse approveDraft(UserAccount admin, ProviderService draft, String note) {
        ProviderService live = draft.getParentService();
        live.setName(draft.getName());
        live.setDescription(draft.getDescription());
        live.setPrice(draft.getPrice());
        live.setCategory(draft.getCategory());
        live.setDurationMin(draft.getDurationMin());
        live.setImagePath(draft.getImagePath());
        live.setReviewNote(note);
        adminServiceRepository.save(live);

        String reason = "Approved changes from draft #" + draft.getId() + (note == null ? "" : ": " + note);
        if (reason.length() > 1000) {
            reason = reason.substring(0, 1000); // audit reason column limit
        }
        adminActionRepository.save(AdminAction.of(admin, AdminActionType.SERVICE_APPROVED,
                AdminTargetType.SERVICE, live.getId(), reason));
        adminServiceRepository.delete(draft);
        return AdminServiceResponse.from(live);
    }

    private ProviderService findService(Long serviceId) {
        return adminServiceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service " + serviceId + " was not found."));
    }
}
