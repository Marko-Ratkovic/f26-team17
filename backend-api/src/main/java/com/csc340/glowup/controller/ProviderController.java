package com.csc340.glowup.controller;

import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.service.ProviderProfileService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {
    
    private final ProviderProfileService providerService;

    public ProviderController(ProviderProfileService providerService){
        this.providerService = providerService;
    }

    @GetMapping("/{providerId}")
    public ProviderResponse getProfile(@PathVariable Long providerId){
        return providerService.getProfile(providerId);
    }

}
