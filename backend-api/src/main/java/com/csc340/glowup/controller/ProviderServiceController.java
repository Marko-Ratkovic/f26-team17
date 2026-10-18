package com.csc340.glowup.controller;

import com.csc340.glowup.dto.ProviderServiceResponse;
import com.csc340.glowup.enums.ServiceCategory;
import com.csc340.glowup.service.ServiceCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController 
@RequestMapping("/api/services")
public class ProviderServiceController {
    private final ServiceCatalogService catalogService;

    public ProviderServiceController(ServiceCatalogService catalogService){
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<ProviderServiceResponse> browse(@RequestParam(required = false) ServiceCategory category){
        if(category == null){
            return catalogService.listApproved();
        }
        return catalogService.listApprovedByCategory(category);
    }

    

}
