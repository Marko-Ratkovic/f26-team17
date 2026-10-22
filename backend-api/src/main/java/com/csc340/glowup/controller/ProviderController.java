package com.csc340.glowup.controller;

import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.dto.ProviderScheduleRequest;
import com.csc340.glowup.enums.Weekday;
import com.csc340.glowup.dto.ProviderScheduleResponse;
import com.csc340.glowup.dto.ProviderServiceRequest;
import com.csc340.glowup.dto.ProviderServiceResponse;
import com.csc340.glowup.service.AppointmentService;
import com.csc340.glowup.service.ProviderProfileService;
import com.csc340.glowup.service.ServiceCatalogService;
import com.csc340.glowup.service.ProviderScheduleService;
import com.csc340.glowup.dto.ProviderUpdateRequest;
import com.csc340.glowup.dto.AppointmentResponse;
import com.csc340.glowup.dto.AppointmentDetailResponse;
import com.csc340.glowup.dto.AppointmentReasonRequest;
import com.csc340.glowup.dto.ProviderRegistrationRequest;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final ProviderScheduleService providerScheduleService;
    private final AppointmentService appointmentService;

    public ProviderController(
            ProviderProfileService providerService,
            ServiceCatalogService catalogService,
            ProviderScheduleService providerScheduleService,
            AppointmentService appointmentService){
        this.providerService = providerService;
        this.catalogService = catalogService;
        this.providerScheduleService = providerScheduleService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/register")
    public ResponseEntity<ProviderResponse> register(@Valid @RequestBody ProviderRegistrationRequest request){
        ProviderResponse provider = providerService.register(request);
        return ResponseEntity.created(URI.create("/api/providers/" + provider.id())).body(provider);
    }

    /** Public profile: 404 unless the provider is ACTIVE (pending, suspended and closed providers are hidden). */
    @GetMapping("/{providerId}")
    public ProviderResponse getPublicProfile(@PathVariable Long providerId){
        return providerService.getPublicProfile(providerId);
    }

    /** The provider's own profile in any status (for their profile page while pending approval). */
    @GetMapping("/{providerId}/profile")
    public ProviderResponse getOwnProfile(@PathVariable Long providerId){
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

    @GetMapping("/{providerId}/all-services")
    public List<ProviderServiceResponse> allServices(@PathVariable Long providerId){
        return catalogService.listServices(providerId);
    }

    @PostMapping("/{providerId}/services")
    public ResponseEntity<ProviderServiceResponse> createService(
            @PathVariable  Long providerId,
            @Valid @RequestBody ProviderServiceRequest request){
        ProviderServiceResponse providerService = catalogService.create(providerId,request);
        return ResponseEntity.created(URI.create("/api/providers/"+providerId
                + "/services/" + providerService.id())).body(providerService);
    }

    @PutMapping("/{providerId}/services/{serviceId}")
    public ProviderServiceResponse updateService(
            @PathVariable  Long providerId,
            @PathVariable Long serviceId,
            @Valid @RequestBody ProviderServiceRequest request){
        return catalogService.update(providerId, serviceId, request);
    }

    @GetMapping("/{providerId}/schedules")
    public List<ProviderScheduleResponse> schedules(
        @PathVariable Long providerId,
        @RequestParam (defaultValue = "false") boolean availableOnly){
        return providerScheduleService.list(providerId,availableOnly);
    }

    /** Sets one weekday's hours, e.g. PUT /api/providers/5/schedules/MONDAY {"isAvailable":true,"openTime":"10:00","closeTime":"19:30"}. */
    @PutMapping("/{providerId}/schedules/{weekday}")
    public ProviderScheduleResponse setScheduleDay(
            @PathVariable Long providerId,
            @PathVariable Weekday weekday,
            @Valid @RequestBody ProviderScheduleRequest request){
        return providerScheduleService.setDay(providerId, weekday, request);
    }

    @GetMapping("/{providerId}/appointments")
    public List<AppointmentResponse> listAppointment(@PathVariable Long providerId){
        return appointmentService.listForProvider(providerId);
    }

    @GetMapping("/{providerId}/appointments/{appointmentId}")
    public ResponseEntity<AppointmentDetailResponse> appointmentDetail(
            @PathVariable Long providerId,
            @PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.getDetail(providerId, appointmentId));
    }

    @PatchMapping("/{providerId}/appointments/{appointmentId}/confirm")
    public AppointmentResponse confirmAppointment(
            @PathVariable Long providerId,
            @PathVariable Long appointmentId){
        return appointmentService.confirm(providerId, appointmentId);
    }

    /** Decline a request or a reschedule request; {"reason": "..."} is required (US-7). */
    @PatchMapping("/{providerId}/appointments/{appointmentId}/decline")
    public AppointmentResponse declineAppointment(
            @PathVariable Long providerId,
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentReasonRequest request){
        return appointmentService.decline(providerId, appointmentId, request.reason());
    }

    /** Cancel a confirmed appointment before it starts; {"reason": "..."} is required. */
    @PatchMapping("/{providerId}/appointments/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable Long providerId,
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentReasonRequest request){
        return appointmentService.cancelByProvider(providerId, appointmentId, request.reason());
    }

    @PatchMapping("/{providerId}/appointments/{appointmentId}/complete")
    public AppointmentResponse completedAppointment(
            @PathVariable Long providerId,
            @PathVariable Long appointmentId){
        return appointmentService.markCompleted(providerId, appointmentId);
    }




}
