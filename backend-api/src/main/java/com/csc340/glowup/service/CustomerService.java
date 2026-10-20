package com.csc340.glowup.service;

import com.csc340.glowup.entity.*;
import com.csc340.glowup.dto.*;
import com.csc340.glowup.enums.*;
import com.csc340.glowup.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.*;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final UserAccountRepository userAccountRepository;
    private final CustomerSafetyNotesRepository safetyNotesRepository;
    private final PreferredTimeRangeRepository timeRangeRepository;
    private final ProviderRepository providerRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderScheduleRepository providerScheduleRepository;
    private final PasswordEncoder passwordEncoder;

        public CustomerService(CustomerRepository customerRepository,
                           UserAccountRepository userAccountRepository,
                           CustomerSafetyNotesRepository safetyNotesRepository,
                           PreferredTimeRangeRepository timeRangeRepository,
                           ProviderRepository providerRepository,
                           ProviderServiceRepository providerServiceRepository,
                           ProviderScheduleRepository providerScheduleRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userAccountRepository = userAccountRepository;
        this.safetyNotesRepository = safetyNotesRepository;
        this.timeRangeRepository = timeRangeRepository;
        this.providerRepository = providerRepository;
        this.providerServiceRepository = providerServiceRepository;
        this.providerScheduleRepository = providerScheduleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //register()
    @Transactional
    public CustomerResponse register(CustomerRegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already registered.");
        }

        UserAccount account = new UserAccount();
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setPhoneNumber(normalizePhone(request.phoneNumber()));
        account.setAccountRole(AccountRole.CUSTOMER);
        account.setAccountStatus(AccountStatus.ACTIVE);
        userAccountRepository.save(account);

        Customer customer = new Customer();
        customer.setName(request.name().trim());
        customer.setUserAccount(account);
        customerRepository.save(customer);

        return CustomerResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found."));
        return CustomerResponse.from(customer);
    }

    //update()
    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer customer = findCustomer(id);
        UserAccount account = customer.getUserAccount();

        String newEmail = request.email().trim().toLowerCase();
        if (!newEmail.equalsIgnoreCase(account.getEmail())) {
            if (userAccountRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new ConflictException("Email already registered.");
            }
            account.setEmail(newEmail);
        }

        customer.setName(request.name().trim());
        // phone number is optional, so null or blank clear it, otherwise normalize and set it.
        account.setPhoneNumber(normalizePhone(request.phoneNumber()));

        return CustomerResponse.from(customer);
    }

    @Transactional
    public SafetyNotesDto updateSafetyNotes(Long id, SafetyNotesDto dto) {
        Customer customer = findCustomer(id);

        CustomerSafetyNotes notes = safetyNotesRepository.findById(customer.getId())
                .orElseGet(() -> {
                    CustomerSafetyNotes created = new CustomerSafetyNotes();
                    created.setCustomer(customer);
                    return created;
                });
        
        boolean hasKnownAllergies = dto.allergyStatus() == AllergyStatus.KNOWN;
        String details = dto.allergyDetails() == null ? null : dto.allergyDetails().trim();
        String sensitivities = (dto.sensitivities() == null || dto.sensitivities().isBlank()) 
                ?  null : dto.sensitivities().trim();
        if (hasKnownAllergies && (details == null || details.isEmpty())) {
            throw new BadRequestException("Allergy details are required when allergy status is KNOWN");
        }

        notes.setAllergyStatus(dto.allergyStatus());
        // details only make sense when allergies are known; clear them otherwise
        notes.setAllergyDetails(hasKnownAllergies ? details : null);
        notes.setSensitivities(sensitivities);

        return SafetyNotesDto.from(safetyNotesRepository.save(notes));
    }

    // changePassword(): customer must know their current password
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        UserAccount account = findCustomer(id).getUserAccount();

        if (!passwordEncoder.matches(request.currentPassword(), account.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), account.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }

        account.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional(readOnly = true)
    public List<PreferredTimeRangeDto> getPreferredTimes(Long id) {
        Customer customer = findCustomer(id);
        return timeRangeRepository.findByCustomerId(customer.getId()).stream()
            .map(PreferredTimeRangeDto::from).toList();
    }

    // replaces the customer's whole list of preferred time ranges
    @Transactional
    public List<PreferredTimeRangeDto> replacePreferredTimes(Long id, List<PreferredTimeRangeDto> dtos) {
        validateRanges(dtos);

        Customer customer = findCustomer(id);
        timeRangeRepository.deleteByCustomerId(customer.getId());
        timeRangeRepository.flush();

        List<PreferredTimeRange> ranges = new ArrayList<>();
        for (PreferredTimeRangeDto dto : dtos) {
            PreferredTimeRange range = new PreferredTimeRange();
            range.setCustomer(customer);
            range.setDayOfWeek(dto.dayOfWeek());
            range.setStartTime(dto.startTime());
            range.setEndTime(dto.endTime());
            ranges.add(range);
        }

        return timeRangeRepository.saveAll(ranges).stream().map(PreferredTimeRangeDto::from).toList();
    }

    // saved providers: saving the same provider twice is harmless (it is a Set)
    @Transactional
    public void saveProvider(Long customerId, Long providerId) {
        Customer customer = findCustomer(customerId);
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found."));
        customer.getSavedProviders().add(provider);
    }
 
    @Transactional
    public void removeSavedProvider(Long customerId, Long providerId) {
        Customer customer = findCustomer(customerId);
        customer.getSavedProviders().removeIf(provider -> provider.getId().equals(providerId));
    }
 
    @Transactional(readOnly = true)
    public List<SavedProviderDto> getSavedProviders(Long customerId) {
        return findCustomer(customerId).getSavedProviders().stream()
                .map(SavedProviderDto::from)
                .toList();
    }

    /* browse(): approved, visible services from active providers, grouped by provider.
     All filters are optional. Rating and sorting come later (they need Review).
       category  - a ServiceCategory name
       city      - exact match, ignoring case
       day       - a Weekday name; provider must be available that day
       timeOfDay - MORNING, AFTERNOON or EVENING; provider must be available in that window
    */
    @Transactional(readOnly = true)
    public List<BrowseProviderDto> browse(String category, String city, String day, String timeOfDay) {
        ServiceCategory wantedCategory = parseCategory(category);
        Weekday wantedDay = parseDay(day);
        LocalTime[] window = parseTimeOfDay(timeOfDay);
        boolean filterByCity = city != null && !city.isBlank();

        List<ProviderService> services =
                providerServiceRepository.findAllByStatusIgnoreCaseAndIsVisibleOrderById("APPROVED", true);

        Map<Long, List<ProviderService>> byProvider = new LinkedHashMap<>();
        for (ProviderService service : services) {
            Provider provider = service.getProvider();

            if (provider.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
                continue;
            }
            if (wantedCategory != null && service.getCategory() != wantedCategory) {
                continue;
            }
            if (filterByCity && !provider.getCity().equalsIgnoreCase(city.trim())) {
                continue;
            }
            byProvider.computeIfAbsent(provider.getId(), key -> new ArrayList<>()).add(service); 
        }

        if (byProvider.isEmpty()) {
            return List.of();
        }

        if (wantedDay != null || window != null) {
            byProvider.keySet().removeIf(providerId -> providerScheduleRepository
                .findAllByProviderIdAndIsAvailableTrueOrderByStartTime(providerId).stream()
                .noneMatch(slot -> (wantedDay == null || slot.getWeekday() == wantedDay)
                        && (window == null
                        || (slot.getStartTime().isBefore(window[1]) && slot.getEndTime().isAfter(window[0])))));
        }
        
        return byProvider.values().stream().map(BrowseProviderDto::from).toList();
    }


    // Helper methods
    
    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found."));
    }

    // strips formatting like spaces, dashes and parentheses; blank becomes null
    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String digits = phone.replaceAll("[^0-9+]", "");
        return digits.isEmpty() ? null : digits;
    }

    private ServiceCategory parseCategory(String category) {
        if (category == null || category.isBlank()) {

        }
        try {
            return ServiceCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new  BadRequestException("Unknown service category.");
        }
    }

    private Weekday parseDay(String day) {
        if (day == null || day.isBlank()) {
            return null;
        }
        try {
            return Weekday.valueOf(day.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown day.");
        }
    }

    // returns [windowStart, windowEnd], or null when no time of day was given
    private LocalTime[] parseTimeOfDay(String timeOfDay) {
        if (timeOfDay == null || timeOfDay.isBlank()) {
            return null;
        }
        return switch (timeOfDay.trim().toUpperCase()) {
            case "MORNING" -> new LocalTime[]{LocalTime.MIN, LocalTime.NOON};
            case "AFTERNOON" -> new LocalTime[]{LocalTime.NOON, LocalTime.of(17, 0)};
            case "EVENING" -> new LocalTime[]{LocalTime.of(17, 0), LocalTime.MAX};
            default -> throw new BadRequestException("Unknown time of day. Use MORNING, AFTERNOON or EVENING.");
        };
    }

    private void validateRanges(List<PreferredTimeRangeDto> dtos) {
        for (PreferredTimeRangeDto d : dtos) {
            if (!d.startTime().isBefore(d.endTime())) {
                throw new BadRequestException(
                    "Start time must be before end time (" + d.dayOfWeek() + ")");
            }
        }
        List<PreferredTimeRangeDto> sorted = new ArrayList<>(dtos);
        sorted.sort(Comparator.comparing(PreferredTimeRangeDto::dayOfWeek)
                .thenComparing(PreferredTimeRangeDto::startTime));
        for (int i = 1; i < sorted.size(); i++) {
            PreferredTimeRangeDto prev = sorted.get(i - 1);
            PreferredTimeRangeDto current = sorted.get(i);
            if (prev.dayOfWeek() == current.dayOfWeek() && current.startTime().isBefore(prev.endTime())) {
                throw new BadRequestException(
                    "Overlapping time ranges on " + current.dayOfWeek());
            }
        }
    }
}
