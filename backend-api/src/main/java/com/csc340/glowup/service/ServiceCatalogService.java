package com.csc340.glowup.service;

import com.csc340.glowup.dto.ProviderServiceRequest;
import com.csc340.glowup.dto.ProviderServiceResponse;
import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.ProviderServiceRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional 
public class ServiceCatalogService {
    private static final Set<String> SERVICE_STATUSES = Set.of("APPROVED", "PENDING", "CHANGES_REQUESTED", "REJECTED", "ARCHIVED");

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
        ProviderService providerService = new ProviderService();
        providerService.setProvider(provider);
        apply(providerService, request, "PENDING",false);
        return ProviderServiceResponse.from(providerServiceRepository.save(providerService));
    }

    public ProviderServiceResponse update(
            Long providerId, Long serviceId, ProviderServiceRequest request){
        ProviderService providerService = findService(serviceId);
        if(!providerService.getProvider().getId().equals(providerId)){
            throw new NotFoundException("Service "+serviceId+" was not found for provider "+providerId+".");
        }
        apply(providerService, request, providerService.getStatus(),providerService.getIsVisible());
        return ProviderServiceResponse.from(providerServiceRepository.save(providerService));

    }

    private Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider "+providerId+" was not found."));
    }

    private void apply(
        ProviderService providerService, ProviderServiceRequest request, String existingStatus, Boolean isVisible){
            providerService.setName(request.name().trim());
            providerService.setDescription(request.description());
            providerService.setPrice(request.price());
            providerService.setCategory(request.category());
            providerService.setDurationMin(request.durationMin());
            providerService.setImagePath(request.imagePath());
            String status = request.status() == null || request.status().isBlank()
                    ? existingStatus : request.status();
            validate(status);
            providerService.setStatus(status.trim().toUpperCase(Locale.ROOT));
            providerService.setIsVisible(
                request.isVisible()==null ? isVisible : request.isVisible());
    }

    private void validate(String status){
        if(!SERVICE_STATUSES.contains(status.trim().toUpperCase(Locale.ROOT))){
            throw new BadRequestException("Service status must be APPROVED, PENDING, or CHANGE_REQUESTED");
        }
    }

    public List<ProviderServiceResponse> listServices(Long providerId){
        ensureProviderExists(providerId);
        return providerServiceRepository
        .findAllByProviderIdOrderById(providerId)
        .stream().map(ProviderServiceResponse::from).toList();
    }


    public List<ProviderServiceResponse> listApproved(){
        return providerServiceRepository
                .findAllByStatusIgnoreCaseAndIsVisibleOrderById("APPROVED",true)
                .stream().map(ProviderServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> listApprovedByCategory(ServiceCategory category){
        return providerServiceRepository
                .findAllByCategoryIgnoreCaseAndStatusIgnoreCaseAndIsVisibleOrderById(category, "APPROVED",true)
                .stream().map(ProviderServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> listApprovedForProvider(Long providerId){
        ensureProviderExists(providerId);
        return providerServiceRepository
                .findAllByProviderIdAndStatusIgnoreCaseAndIsVisibleOrderById(providerId, "APPROVED",true)
                .stream().map(ProviderServiceResponse::from).toList();
    }

    private ProviderService findService(Long serviceId){
        return providerServiceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException(
                    "Service "+serviceId+" was not found."));
    }

    private void ensureProviderExists(Long providerId){
        if(!providerRepository.existsById(providerId))
            throw new NotFoundException("Provider "+providerId+" was not found.");
    }

}
