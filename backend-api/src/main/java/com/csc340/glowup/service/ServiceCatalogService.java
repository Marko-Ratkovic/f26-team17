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
public class ServiceCatalogService {
    private static final Set<String> SERVICE_STATUSES = Set.of("APPROVED", "PENDING", "CHANGE_REQUESTED");

    private ProviderRepository providerRepository;
    private ProviderServiceRepository providerServiceRepository;

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
        apply(providerService, request, "PENDING");
        return ProviderServiceResponse.from(providerServiceRepository.save(providerService));
    }

    private Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider "+providerId+" was not found."));
    }

    private void apply(
        ProviderService providerService, ProviderServiceRequest request, String existingStatus){
            providerService.setName(request.name().trim());
            providerService.setDescription(request.description());
            providerService.setPrice(request.price());
            providerService.setCategory(request.category());
            providerService.setDurationMin(request.durationMin());
            providerService.setImagePath(request.imagePath());
            String status = request.status() == null || request.status().isBlank()
                    ? existingStatus : request.status();
            providerService.setStatus(status.trim().toUpperCase(Locale.ROOT));
    }

    public List<ProviderServiceResponse> listApproved(){
        return providerServiceRepository
                .findAllByStatusIgnoreCaseOrderById("APPROVED")
                .stream().map(ProviderServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> listApprovedByCategory(ServiceCategory category){
        return providerServiceRepository
                .findAllByCategoryIgnoreCaseAndStatusIgnoreCaseOrderById(category, "APPROVED")
                .stream().map(ProviderServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> listApprovedForProvider(Long providerId){
        ensureProviderExists(providerId);
        return providerServiceRepository
                .findAllByProviderIdAndStatusIgnoreCaseOrderById(providerId, "APPROVED")
                .stream().map(ProviderServiceResponse::from).toList();
    }

    private void ensureProviderExists(Long providerId){
        if(!providerRepository.existsById(providerId))
            throw new NotFoundException("Provider "+providerId+" was not found.");
    }

}
