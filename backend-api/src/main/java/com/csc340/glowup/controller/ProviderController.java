package com.csc340.glowup.controller;

import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.dto.ProviderServiceRequest;
import com.csc340.glowup.dto.ProviderServiceResponse;
import com.csc340.glowup.service.ProviderProfileService;
import com.csc340.glowup.service.ServiceCatalogService;
import com.csc340.glowup.dto.ProviderUpdateRequest;
import com.csc340.glowup.dto.ProviderRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {
    
    private final ProviderProfileService providerService;
    private final ServiceCatalogService catalogService;

    public ProviderController(
        ProviderProfileService providerService,
        ServiceCatalogService catalogService){
        this.providerService = providerService;
        this.catalogService = catalogService;
    }

    @PostMapping
    public ResponseEntity<ProviderResponse> register(@Valid @RequestBody ProviderRegistrationRequest request){
        ProviderResponse provider = providerService.register(request);
        return ResponseEntity.created(URI.create("/api/providers/" + provider.id())).body(provider);
    }

    @GetMapping("/{providerId}")
    public ProviderResponse getProfile(@PathVariable Long providerId){
        return providerService.getProfile(providerId);
    }

    @PutMapping("/{providerId}")
    public ProviderResponse updateProfile(
        @PathVariable Long providerId,
        @Valid @RequestBody ProviderUpdateRequest request) {
            return providerService.updateProfile(providerId,request);
        }

    @GetMapping("/{providerId}/services")
    public List<ProviderServiceResponse> services(@PathVariable Long providerId){
        return catalogService.listApprovedForProvider(providerId);
    }

    @PostMapping("/{providerId}/services")
    public ResponseEntity<ProviderServiceResponse> createService(
            @PathVariable  Long providerId,
            @Valid @RequestBody ProviderServiceRequest request){
        ProviderServiceResponse providerService = catalogService.create(providerId,request);
        return ResponseEntity.created(URI.create("api/providers/"+providerId
                + "/services/" + providerService.id())).body(providerService);
    }
}
