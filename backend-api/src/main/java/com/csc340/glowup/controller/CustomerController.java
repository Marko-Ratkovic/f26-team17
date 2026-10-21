package com.csc340.glowup.controller;

import com.csc340.glowup.dto.*;
import com.csc340.glowup.service.AppointmentService;
import com.csc340.glowup.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    private final CustomerService customerService;
    private final AppointmentService appointmentService;

    public CustomerController(CustomerService customerService,
        AppointmentService appointmentService) {
        this.customerService = customerService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.register(request));
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.update(id, request);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, 
                                               @Valid @RequestBody ChangePasswordRequest request) {
        customerService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/safety-notes")
    public SafetyNotesDto updateSafetyNotes(@PathVariable Long id, @Valid @RequestBody SafetyNotesDto dto) {
        return customerService.updateSafetyNotes(id, dto);
    }

 
 
    @GetMapping("/{id}/saved-providers")
    public List<SavedProviderDto> getSavedProviders(@PathVariable Long id) {
        return customerService.getSavedProviders(id);
    }
 
    @PutMapping("/{id}/saved-providers/{providerId}")
    public ResponseEntity<Void> saveProvider(@PathVariable Long id, @PathVariable Long providerId) {
        customerService.saveProvider(id, providerId);
        return ResponseEntity.noContent().build();
    }
 
    @DeleteMapping("/{id}/saved-providers/{providerId}")
    public ResponseEntity<Void> removeSavedProvider(@PathVariable Long id, @PathVariable Long providerId) {
        customerService.removeSavedProvider(id, providerId);
        return ResponseEntity.noContent().build();
    }
 
    @GetMapping("/browse")
    public List<BrowseProviderDto> browse(@RequestParam(required = false) String category,
                                          @RequestParam(required = false) String city,
                                          @RequestParam(required = false) String day,
                                          @RequestParam(required = false) String timeOfDay,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "false") boolean savedOnly,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) String sort) {
        return customerService.browse(category, city, day, timeOfDay, keyword, savedOnly, customerId, sort);
    }

    /** The customer's appointment history, newest first (US-3). */
    @GetMapping("/{customerId}/appointments")
    public List<AppoinmentResponse> listAppointments(@PathVariable Long customerId) {
        return appointmentService.listForCustomer(customerId);
    }

    @PostMapping("/{customerId}/appointments")
    public ResponseEntity<AppoinmentResponse> bookAppointment(
                @PathVariable Long customerId,
                @Valid @RequestBody BookAppointmentRequest request){
        AppoinmentResponse appointment = appointmentService.book(customerId, request);
        return ResponseEntity.created(URI.create("/api/customers/" + customerId
                + "/appointments/" + appointment.id())).body(appointment);
    }

    /** Cancel a request or a confirmed appointment before it starts; the body {"reason": "..."} is optional. */
    @PatchMapping("/{customerId}/appointments/{appointmentId}/cancel")
    public AppoinmentResponse cancelAppointment(
                @PathVariable Long customerId,
                @PathVariable Long appointmentId,
                @Valid @RequestBody(required = false) AppointmentReasonRequest request){
        return appointmentService.cancelByCustomer(customerId, appointmentId, request == null ? null : request.reason());
    }

    /** Ask for a new time for a confirmed appointment (US-3); the original stays confirmed until the provider answers. */
    @PostMapping("/{customerId}/appointments/{appointmentId}/reschedule")
    public ResponseEntity<AppoinmentResponse> requestReschedule(
                @PathVariable Long customerId,
                @PathVariable Long appointmentId,
                @Valid @RequestBody RescheduleRequest request){
        AppoinmentResponse newTime = appointmentService.requestReschedule(customerId, appointmentId, request);
        return ResponseEntity.created(URI.create("/api/customers/" + customerId
                + "/appointments/" + newTime.id())).body(newTime);
    }
}
