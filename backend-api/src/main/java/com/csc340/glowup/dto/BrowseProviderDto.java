package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.enums.ServiceCategory;
 
import java.math.BigDecimal;
import java.util.List;
 
public record BrowseProviderDto(
        Long providerId,
        String name,
        String city,
        String state,
        List<ServiceCategory> categories,
        Integer yearsOfExperience,
        String imagePath,
        List<ServiceSummary> services
) {
 
    public record ServiceSummary(
            Long id,
            String name,
            BigDecimal price,
            ServiceCategory category,
            Integer durationMin
    ) {
        public static ServiceSummary from(ProviderService service) {
            return new ServiceSummary(
                    service.getId(),
                    service.getName(),
                    service.getPrice(),
                    service.getCategory(),
                    service.getDurationMin());
        }
    }
 
    public static BrowseProviderDto from(List<ProviderService> providerServices) {
        Provider provider = providerServices.get(0).getProvider();
        return new BrowseProviderDto(
                provider.getId(),
                provider.getName(),
                provider.getCity(),
                provider.getState(),
                providerServices.stream().map(ProviderService::getCategory).distinct().toList(),
                provider.getYearsOfExperience(),
                provider.getImagePath(),
                providerServices.stream().map(ServiceSummary::from).toList());
    }
}
