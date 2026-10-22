package com.csc340.glowup.service;

import com.csc340.glowup.dto.ProviderServiceRequest;
import com.csc340.glowup.dto.ProviderServiceResponse;
import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.ServiceStatus;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ProviderServiceRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Providers create and edit services; only admins approve them (US-6, US-10).
 * A provider can never set a service's status or visibility directly:
 * every new or edited service goes to PENDING and stays hidden until an admin approves it.
 */
@Service
@Transactional
public class ServiceCatalogService {

    private final ProviderRepository providerRepository;
    private final ProviderServiceRepository providerServiceRepository;

    public ServiceCatalogService(
        ProviderServiceRepository providerServiceRepository,
        ProviderRepository providerRepository){
            this.providerServiceRepository = providerServiceRepository;
            this.providerRepository = providerRepository;
    }

    public ProviderServiceResponse create(Long providerId, ProviderServiceRequest request){
        Provider provider = findProvider(providerId);
        if(provider.getUserAccount().getAccountStatus()!=AccountStatus.ACTIVE){
            throw new ForbiddenException("Your account's status is "+provider.getUserAccount().getAccountStatus()+" and cannot create service.");
        }
        ProviderService providerService = new ProviderService();
        providerService.setProvider(provider);
        providerService.setParentService(null);
        apply(providerService, request);
        submitForReview(providerService);
        return ProviderServiceResponse.from(providerServiceRepository.save(providerService));
    }

    /** Any edit sends the service back to review. Rejected and archived services cannot be edited. */
    public ProviderServiceResponse update(
            Long providerId, Long serviceId, ProviderServiceRequest request){
        ProviderService providerService = providerServiceRepository.findByIdForUpdate(serviceId)
                .orElseThrow(() -> new NotFoundException("Service "+serviceId+" was not found."));
        if(!providerService.getProvider().getId().equals(providerId)){
            throw new NotFoundException("Service "+serviceId+" was not found for provider "+providerId+".");
        }
        if(findProvider(providerId).getUserAccount().getAccountStatus()!=AccountStatus.ACTIVE){
            throw new ForbiddenException("Your account's status is "+findProvider(providerId).getUserAccount().getAccountStatus()+" and cannot create service.");
        }
        if(providerService.getStatus() == ServiceStatus.PENDING){
            throw new ConflictException("An update for this service is already pending for admin review");
        }
        if (providerService.getStatus() == ServiceStatus.REJECTED
                || providerService.getStatus() == ServiceStatus.ARCHIVED) {
            throw new ConflictException("A " + providerService.getStatus() + " service cannot be edited.");
        }
        if(providerService.getStatus() == ServiceStatus.APPROVED){
            // one open draft at a time: a draft sent back with CHANGES_REQUESTED is still open (edit that draft instead)
            if(providerServiceRepository.existsByParentServiceIdAndStatus(providerService.getId(),ServiceStatus.PENDING)
                    || providerServiceRepository.existsByParentServiceIdAndStatus(providerService.getId(),ServiceStatus.CHANGES_REQUESTED)){
                throw new ConflictException("An update for this service is already waiting for admin review");
            }
            ProviderService draft = new ProviderService();
            draft.setProvider(providerService.getProvider());
            draft.setParentService(providerService);
            apply(draft,request);
            submitForReview(draft);
            return ProviderServiceResponse.from(providerServiceRepository.save(draft));
        }
        apply(providerService, request);
        submitForReview(providerService);
        return ProviderServiceResponse.from(providerServiceRepository.save(providerService));
    }

    private Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider "+providerId+" was not found."));
    }

    private void apply(ProviderService providerService, ProviderServiceRequest request){
            providerService.setName(request.name().trim());
            providerService.setDescription(request.description());
            providerService.setPrice(request.price());
            providerService.setCategory(request.category());
            providerService.setDurationMin(request.durationMin());
            providerService.setImagePath(request.imagePath());
    }

    private void submitForReview(ProviderService providerService) {
        providerService.setReviewNote(null); // the admin's old note no longer applies to the new submission
        providerService.setStatus(ServiceStatus.PENDING);
        providerService.setIsVisible(false);
    }

    /** All of a provider's own services in every status (for the provider's "My services" page). */
    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> listServices(Long providerId){
        ensureProviderExists(providerId);
        return providerServiceRepository
        .findAllByProviderIdOrderById(providerId)
        .stream().map(ProviderServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> listApprovedForProvider(Long providerId){
        ensureProviderExists(providerId);
        return providerServiceRepository.findPublic(null, providerId)
                .stream().map(ProviderServiceResponse::from).toList();
    }

    private void ensureProviderExists(Long providerId){
        if(!providerRepository.existsById(providerId))
            throw new NotFoundException("Provider "+providerId+" was not found.");
    }
}
