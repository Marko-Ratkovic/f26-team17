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
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderScheduleRepository providerScheduleRepository;
    private final ProviderRepository providerRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            CustomerRepository customerRepository,
            ProviderServiceRepository providerServiceRepository,
            ProviderScheduleRepository providerScheduleRepository,
            ProviderRepository providerRepository){
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.providerServiceRepository = providerServiceRepository;
        this.providerScheduleRepository = providerScheduleRepository;
        this.providerRepository = providerRepository;
    }

    public AppointmentResponse book(Long customerId, BookAppointmentRequest request){
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Customer "+customerId+" was not found"));
        ProviderService service = providerServiceRepository.findById(request.serviceId())
            .orElseThrow(() -> new NotFoundException("Service "+request.serviceId()+" was not found"));
        requireBookableService(service);
        requireBookableTime(service, service.getDurationMin(), request.startDateTime(), null);
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setService(service);
        appointment.setStartsAt(request.startDateTime());
        appointment.setDurationMin(service.getDurationMin());
        appointment.setPrice(service.getPrice());
        appointment.setStatus(AppointmentStatus.REQUESTED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listForCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer " + customerId + " was not found.");
        }
        return appointmentRepository.findAllByCustomerIdOrderByIdDesc(customerId)
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listForProvider(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new NotFoundException("Provider " + providerId + " was not found.");
        }
        return appointmentRepository.findAllByProviderService_Provider_IdOrderByIdDesc(providerId)
                .stream().map(AppointmentResponse::from).toList();
    }

    public AppointmentResponse confirm(Long providerId, Long appointmentId){
        Appointment appointment = findForProvider(providerId, appointmentId);
        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            throw new ConflictException("Only requested appointments can be confirmed.");
        }
        if (!appointment.getStartsAt().isAfter(LocalDateTime.now(Appointment.TIME_ZONE))) {
            throw new ConflictException("This request's time has already passed.");
        }
        Appointment original = appointment.getRescheduledFrom();
        if (original != null && (original.getStatus() != AppointmentStatus.CONFIRMED || hasStarted(original))) {
            throw new ConflictException("The original appointment is no longer upcoming, so this new time cannot be accepted.");
        }
        // confirming is what prevents double-booking (US-7): requests may overlap, confirmed ones may not.
        // For a reschedule the original slot is being given up, so it does not count as a clash.
        Long originalId = original == null ? null : original.getId();
        if (overlapsConfirmed(providerId, appointment.getStartsAt(), appointment.getEndsAt(), appointment.getId(), originalId)) {
            throw new ConflictException("This time overlaps another confirmed appointment.");
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (original != null) {
            // US-3: on accept the original becomes RESCHEDULED and the new time CONFIRMED
            original.setStatus(AppointmentStatus.RESCHEDULED);
            original.setDecidedAt(now);
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setDecidedAt(now);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    /** US-7: declining a request (or a reschedule request) needs a reason; a declined reschedule leaves the original as it was. */
    public AppointmentResponse decline(Long providerId, Long appointmentId, String reason) {
        Appointment appointment = findForProvider(providerId, appointmentId);
        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            throw new ConflictException("Only requested appointments can be declined.");
        }
        appointment.setStatus(AppointmentStatus.DECLINED);
        appointment.setDecidedAt(OffsetDateTime.now());
        appointment.setStatusReason(requireReason(reason, "decline"));
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    /** A provider can cancel a confirmed appointment before it starts, with a reason for the customer. */
    public AppointmentResponse cancelByProvider(Long providerId, Long appointmentId, String reason) {
        Appointment appointment = findForProvider(providerId, appointmentId);
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ConflictException("Only confirmed appointments can be cancelled; decline requests instead.");
        }
        cancel(appointment, AccountRole.PROVIDER, requireReason(reason, "cancel"));
        return AppointmentResponse.from(appointment);
    }

    /** A customer can cancel a request or a confirmed appointment before it starts; the reason is optional. */
    public AppointmentResponse cancelByCustomer(Long customerId, Long appointmentId, String reason) {
        Appointment appointment = findForCustomer(customerId, appointmentId);
        if (appointment.getStatus() != AppointmentStatus.REQUESTED
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ConflictException("Only requested or confirmed appointments can be cancelled.");
        }
        cancel(appointment, AccountRole.CUSTOMER, reason == null || reason.isBlank() ? null : reason.trim());
        return AppointmentResponse.from(appointment);
    }

    /**
     * US-3: the customer asks for a new time for a confirmed appointment. The new time is saved as its own
     * REQUESTED appointment linked to the original; the original stays CONFIRMED until the provider answers.
     */
    public AppointmentResponse requestReschedule(Long customerId, Long appointmentId, RescheduleRequest request) {
        Appointment original = findForCustomer(customerId, appointmentId);
        if (original.getStatus() != AppointmentStatus.CONFIRMED || hasStarted(original)) {
            throw new ConflictException("Only upcoming confirmed appointments can be rescheduled.");
        }
        if (!appointmentRepository.findAllByRescheduledFromIdAndStatus(appointmentId, AppointmentStatus.REQUESTED).isEmpty()) {
            throw new ConflictException("A new time was already requested for this appointment; wait for the provider or cancel that request.");
        }
        requireBookableService(original.getService());
        requireBookableTime(original.getService(), original.getBookedDurationMin(), request.startDateTime(), original.getId());
        Appointment newTime = new Appointment();
        newTime.setCustomer(original.getCustomer());
        newTime.setService(original.getService());
        newTime.setStartsAt(request.startDateTime());
        // the same service at the same terms as the booking being moved
        newTime.setDurationMin(original.getBookedDurationMin());
        newTime.setPrice(original.getBookedPrice());
        newTime.setStatus(AppointmentStatus.REQUESTED);
        newTime.setRescheduledFrom(original);
        return AppointmentResponse.from(appointmentRepository.save(newTime));
    }

    public AppointmentResponse markCompleted(Long providerId, Long appointmentId){
        Appointment appointment = findForProvider(providerId, appointmentId);
        ensureBooked(appointment);
        if (!appointment.hasEnded()) {
            throw new ConflictException("An appointment can only be marked completed after it ends.");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(OffsetDateTime.now());
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentDetailResponse getDetail(Long providerId,Long appointmentId){
        Appointment appointment = findForProvider(providerId, appointmentId);
        return AppointmentDetailResponse.from(appointment);
    }

    public int cancelUpcomingForAccount(UserAccount account, String reason) {
        int cancelled = 0;
        for (Appointment appointment : appointmentRepository.findUpcomingOpenForUser(
                account.getId(), LocalDateTime.now(Appointment.TIME_ZONE))) {
            // its reschedule request may already be cancelled along with it
            if (appointment.getStatus() == AppointmentStatus.REQUESTED
                    || appointment.getStatus() == AppointmentStatus.CONFIRMED) {
                cancel(appointment, AccountRole.ADMIN, reason);
                cancelled++;
            }
        }
        return cancelled;
    }

    public int declineRequestsForArchivedService(Long serviceId) {
        List<Appointment> requests = appointmentRepository.findUpcomingRequestsForService(
                serviceId, LocalDateTime.now(Appointment.TIME_ZONE));
        OffsetDateTime now = OffsetDateTime.now();
        for (Appointment request : requests) {
            request.setStatus(AppointmentStatus.DECLINED);
            request.setDecidedAt(now);
            request.setStatusReason("This service is no longer offered.");
        }
        return requests.size();
    }

    /** Only approved, visible services of ACTIVE providers can be booked or moved (US-3, US-5). */
    private static void requireBookableService(ProviderService service) {
        if (service.getStatus() != ServiceStatus.APPROVED || !Boolean.TRUE.equals(service.getIsVisible())
                || service.getProvider().getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("This service is not available for booking.");
        }
    }

    /** Shared by booking and rescheduling: in the future, inside opening hours, no clash with a confirmed appointment. */
    private void requireBookableTime(ProviderService service, int durationMin, LocalDateTime start, Long ignoreAppointmentId) {
        if (!start.isAfter(LocalDateTime.now(Appointment.TIME_ZONE))) {
            throw new BadRequestException("Please choose a time in the future.");
        }
        if (!ProviderScheduleService.isOnQuarterHour(start.toLocalTime())) {
            throw new BadRequestException("Start times are in 15-minute steps, e.g. 4:00, 4:15, 4:30 or 4:45.");
        }
        Long providerId = service.getProvider().getId();
        LocalDateTime end = start.plusMinutes(durationMin);
        requireWithinOpeningHours(providerId, start, end);
        if (overlapsConfirmed(providerId, start, end, ignoreAppointmentId, null)) {
            throw new ConflictException("This time overlaps with an existing booking.");
        }
    }

    private void cancel(Appointment appointment, AccountRole by, String reason) {
        if (hasStarted(appointment)) {
            throw new ConflictException("An appointment can only be cancelled before it starts.");
        }
        OffsetDateTime now = OffsetDateTime.now();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(now);
        appointment.setCancelledBy(by);
        appointment.setStatusReason(reason);
        // a pending request for a new time goes away with the appointment it would replace
        for (Appointment pending : appointmentRepository.findAllByRescheduledFromIdAndStatus(
                appointment.getId(), AppointmentStatus.REQUESTED)) {
            pending.setStatus(AppointmentStatus.CANCELLED);
            pending.setCancelledAt(now);
            pending.setCancelledBy(by);
            pending.setStatusReason("The original appointment was cancelled.");
        }
    }

    private static String requireReason(String reason, String action) {
        if (reason == null || reason.isBlank()) {
            throw new BadRequestException("A reason is required to " + action + ".");
        }
        return reason.trim();
    }

    private static boolean hasStarted(Appointment appointment) {
        return !appointment.getStartsAt().isAfter(LocalDateTime.now(Appointment.TIME_ZONE));
    }

    private Appointment findForProvider(Long providerId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment "+appointmentId+" was not found"));
        if (!appointment.getProvider().getId().equals(providerId)) {
            // 404, not 403: another provider's appointment is treated as not existing, so ids reveal nothing
            throw new NotFoundException("Appointment "+appointmentId+" was not found.");
        }
        return appointment;
    }

    private Appointment findForCustomer(Long customerId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment "+appointmentId+" was not found"));
        if (!appointment.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Appointment "+appointmentId+" was not found.");
        }
        return appointment;
    }

    /**
     * The whole appointment has to fit inside the provider's opening hours for that weekday (US-7):
     * start at or after opening time, end at or before closing time, on the same day.
     */
    private void requireWithinOpeningHours(Long providerId, LocalDateTime start, LocalDateTime end) {
        Weekday weekday = Weekday.valueOf(start.getDayOfWeek().name());
        ProviderSchedule day = providerScheduleRepository.findByProviderIdAndWeekday(providerId, weekday)
                .filter(found -> Boolean.TRUE.equals(found.getIsAvailable()))
                .orElseThrow(() -> new BadRequestException("The provider is closed on " + weekday + "."));
        if (!end.toLocalDate().equals(start.toLocalDate()) || !day.covers(start.toLocalTime(), end.toLocalTime())) {
            throw new BadRequestException("The whole service must fit inside the provider's hours on " + weekday
                    + " (" + day.getOpenTime() + "–" + day.getCloseTime() + ").");
        }
    }

    /** True if [start, end) overlaps one of the provider's CONFIRMED appointments that day, ignoring the given ids. */
    private boolean overlapsConfirmed(Long providerId, LocalDateTime start, LocalDateTime end, Long ignoreId, Long alsoIgnoreId) {
        return appointmentRepository.findConfirmedForProviderBetween(providerId,
                        start.toLocalDate().atStartOfDay(), start.toLocalDate().plusDays(1).atStartOfDay())
                .stream()
                .filter(other -> !other.getId().equals(ignoreId) && !other.getId().equals(alsoIgnoreId))
                .anyMatch(other -> start.isBefore(other.getEndsAt()) && end.isAfter(other.getStartsAt()));
    }

    private void ensureBooked(Appointment appointment) {
        if (appointment.getStatus()!=AppointmentStatus.CONFIRMED) {
            throw new ConflictException("Only confirmed appointment can be changed.");
        }
    }
}
