package com.csc340.glowup.service;

import com.csc340.glowup.entity.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
        Long providerId = service.getProvider().getId();
        LocalDateTime startDateTime = request.startDateTime();
        LocalDateTime endDateTime = startDateTime.plusMinutes(service.getDurationMin());
        Weekday weekday = Weekday.valueOf(startDateTime.getDayOfWeek().name());
        TimeBlock timeBlock = mapTimeToBlock(startDateTime.toLocalTime());
        boolean isBlockAvailable = providerScheduleRepository.existsByProviderIdAndWeekdayAndTimeBlockAndIsAvailable(
            providerId, weekday, timeBlock, true);
        if (!isBlockAvailable){
            throw new BadRequestException("The selected start time falls outside the provider's available schedule blocks.");
        }
        boolean hasOverlap = appointmentRepository.existsConflictingAppointment(
            providerId, startDateTime, endDateTime);

        if (hasOverlap) {
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
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setDecidedAt(OffsetDateTime.now());
        return AppoinmentResponse.from(appointmentRepository.save(appointment));
    }


    private TimeBlock mapTimeToBlock(LocalTime time) {
        if (time.isBefore(LocalTime.of(12, 0))) {
            return TimeBlock.MORNING;
        } else if (time.isBefore(LocalTime.of(17, 0))) {
            return TimeBlock.AFTERNOON;
        } else {
            return TimeBlock.EVENING;
        }
    }
}
