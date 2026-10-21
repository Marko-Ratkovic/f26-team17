package com.csc340.glowup.service;

import com.csc340.glowup.entity.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.csc340.glowup.dto.*;
import com.csc340.glowup.enums.*;
import com.csc340.glowup.repository.*;

@Service 
@Transactional
public class AppointmentService {
    private final ApppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderScheduleRepository providerScheduleRepository;

    public AppointmentService(
            ApppointmentRepository apppointmentRepository,
            CustomerRepository customerRepository,
            ProviderServiceRepository providerServiceRepository,
            ProviderScheduleRepository providerScheduleRepository){
        this.appointmentRepository = apppointmentRepository;
        this.customerRepository = customerRepository;
        this.providerServiceRepository = providerServiceRepository;
        this.providerScheduleRepository = providerScheduleRepository;
    }

    public AppoinmentResponse book(Long customerId, BookAppointmentRequest request){
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Customer "+customerId+" was not found"));
        ProviderService service = providerServiceRepository.findById(request.serviceId())
            .orElseThrow(() -> new NotFoundException("Service "+request.serviceId()+" was not found"));
        // only approved, visible services of ACTIVE providers can be booked (US-3, US-5)
        if (service.getStatus() != ServiceStatus.APPROVED || !Boolean.TRUE.equals(service.getIsVisible())
                || service.getProvider().getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("This service is not available for booking.");
        }
        Long providerId = service.getProvider().getId();
        LocalDateTime startDateTime = request.startDateTime();
        if (!startDateTime.isAfter(LocalDateTime.now(Appointment.TIME_ZONE))) {
            throw new BadRequestException("Please choose a time in the future.");
        }
        LocalDateTime endDateTime = startDateTime.plusMinutes(service.getDurationMin());
        Weekday weekday = Weekday.valueOf(startDateTime.getDayOfWeek().name());
        TimeBlock timeBlock = TimeBlock.of(startDateTime.toLocalTime());
        if (timeBlock == null) {
            throw new BadRequestException("Appointments can start between 9 AM and 9 PM.");
        }
        // the whole service has to fit inside the block it starts in (US-7)
        if (!endDateTime.toLocalDate().equals(startDateTime.toLocalDate())
                || endDateTime.toLocalTime().isAfter(timeBlock.end())) {
            throw new BadRequestException("The whole service must finish by " + timeBlock.end()
                    + ", the end of the " + timeBlock + " block.");
        }
        boolean isBlockAvailable = providerScheduleRepository.existsByProviderIdAndWeekdayAndTimeBlockAndIsAvailable(
            providerId, weekday, timeBlock, true);
        if (!isBlockAvailable){
            throw new BadRequestException("The selected start time falls outside the provider's available schedule blocks.");
        }
        if (overlapsConfirmed(providerId, startDateTime, endDateTime, null)) {
            throw new BadRequestException("This time slot overlaps with an existing booking.");
        }
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setService(service);
        appointment.setStartsAt(request.startDateTime());
        appointment.setStatus(AppointmentStatus.REQUESTED);
        return AppoinmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppoinmentResponse> listForCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer " + customerId + " was not found.");
        }
        return appointmentRepository.findAllByCustomerIdOrderByIdDesc(customerId)
                .stream().map(AppoinmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AppoinmentResponse> listForProvider(Long providerId) {
        return appointmentRepository.findAllByProviderService_Provider_IdOrderByIdDesc(providerId)
                .stream().map(AppoinmentResponse::from).toList();
    }

    public AppoinmentResponse confirm(Long providerId, Long appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment "+appointmentId+" was not found"));
        if (!appointment.getProvider().getId().equals(providerId)) {
            throw new ForbiddenException("You do not have permission to confirm this appointment.");
        }
        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            throw new BadRequestException("Only appointments with REQUESTED status can be confirmed.");
        }
        if (!appointment.getStartsAt().isAfter(LocalDateTime.now(Appointment.TIME_ZONE))) {
            throw new ConflictException("This request's time has already passed.");
        }
        // confirming is what prevents double-booking (US-7): requests may overlap, confirmed ones may not
        if (overlapsConfirmed(providerId, appointment.getStartsAt(), appointment.getEndsAt(), appointment.getId())) {
            throw new ConflictException("This time overlaps another confirmed appointment.");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setDecidedAt(OffsetDateTime.now());
        return AppoinmentResponse.from(appointmentRepository.save(appointment));
    }

    public AppoinmentResponse markCompleted(Long providerId, Long appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment "+appointmentId+" was not found"));
        if (!appointment.getProvider().getId().equals(providerId)) {
            throw new ForbiddenException("Appointment "+appointmentId+" was not found for provider "+providerId+".");
        }
        ensureBooked(appointment);
        if (!appointment.hasEnded()) {
            throw new ConflictException("An appointment can only be marked completed after it ends.");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(OffsetDateTime.now());
        return AppoinmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentDetailResponse getDetail(Long providerId,Long appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment "+appointmentId+" was not found"));
        if (!appointment.getProvider().getId().equals(providerId)) {
            throw new ForbiddenException("Appointment "+appointmentId+" was not found for provider "+providerId+".");
        }
        return AppointmentDetailResponse.from(appointment);
    }

    /** True if [start, end) overlaps one of the provider's CONFIRMED appointments that day (ignoreId = the one being confirmed). */
    private boolean overlapsConfirmed(Long providerId, LocalDateTime start, LocalDateTime end, Long ignoreId) {
        return appointmentRepository.findConfirmedForProviderBetween(providerId,
                        start.toLocalDate().atStartOfDay(), start.toLocalDate().plusDays(1).atStartOfDay())
                .stream()
                .filter(other -> !other.getId().equals(ignoreId))
                .anyMatch(other -> start.isBefore(other.getEndsAt()) && end.isAfter(other.getStartsAt()));
    }

    private void ensureBooked(Appointment appointment) {
        if (appointment.getStatus()!=AppointmentStatus.CONFIRMED) {
            throw new ConflictException("Only confirmed appointment can be changed.");
        }
    }
}
