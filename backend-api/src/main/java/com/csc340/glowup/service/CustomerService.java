package com.csc340.glowup.service;

import com.csc340.glowup.entity.*;
import com.csc340.glowup.dto.*;
import com.csc340.glowup.enums.*;
import com.csc340.glowup.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final UserAccountRepository userAccountRepository;
    private final CustomerSafetyNotesRepository safetyNotesRepository;
    private final ProviderRepository providerRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderScheduleRepository providerScheduleRepository;
    private final PasswordEncoder passwordEncoder;

        public CustomerService(CustomerRepository customerRepository,
                           UserAccountRepository userAccountRepository,
                           CustomerSafetyNotesRepository safetyNotesRepository,
                           ProviderRepository providerRepository,
                           ProviderServiceRepository providerServiceRepository,
                           ProviderScheduleRepository providerScheduleRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userAccountRepository = userAccountRepository;
        this.safetyNotesRepository = safetyNotesRepository;
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

    // saved providers: saving the same provider twice is harmless (it is a Set)
    @Transactional
    public void saveProvider(Long customerId, Long providerId) {
        Customer customer = findCustomer(customerId);
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found."));
        // only ACTIVE providers can be saved; anything else looks the same as a missing provider
        if (provider.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new NotFoundException("Provider not found.");
        }
        customer.getSavedProviders().add(provider);
    }
 
    @Transactional
    public void removeSavedProvider(Long customerId, Long providerId) {
        Customer customer = findCustomer(customerId);
        customer.getSavedProviders().removeIf(provider -> provider.getId().equals(providerId));
    }
 
    @Transactional(readOnly = true)
    public List<SavedProviderDto> getSavedProviders(Long customerId) {
        // inasctive providers stay saved but are hidden; they reappear if the provider is reinstated
        return findCustomer(customerId).getSavedProviders().stream()
                .filter(provider -> provider.getUserAccount().getAccountStatus() == AccountStatus.ACTIVE)
                .map(SavedProviderDto::from)
                .toList();
    }

    /* browse(): approved, visible services from active providers, grouped by provider.
     All filters are optional and can be combined:
       category   - a ServiceCategory name
       city       - exact match, ignoring case
       day        - a Weekday name; provider must be available that day
       timeOfDay  - MORNING (9–12), AFTERNOON (12–5) or EVENING (5–9): the provider is open at some
                    point in that range (on the chosen day, or on any day when no day is given)
       keyword    - matches the provider name, or a service name or description
       savedOnly  - true to show only the customer's saved providers (needs customerId)
       customerId - whose saved providers to use with savedOnly
       sort       - NEWEST puts the most recently created services first. Without a sort the order
                    changes once a day, so no provider is always first. Rating sorts come with Review.
    */
    @Transactional(readOnly = true)
        public List<BrowseProviderDto> browse(String category, String city, String day, String timeOfDay,
                                          String keyword, boolean savedOnly, Long customerId, String sort) {
        ServiceCategory wantedCategory = parseCategory(category);
        Weekday wantedDay = parseDay(day);
        TimeBlock wantedBlock = parseTimeOfDay(timeOfDay);
        BrowseSort wantedSort = parseSort(sort);
        boolean filterByCity = city != null && !city.isBlank();
        String wantedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim().toLowerCase();
        Set<Long> savedProviderIds = savedOnly ? savedProviderIds(customerId) : null;

        List<ProviderService> services =
                providerServiceRepository.findPublic(wantedCategory, null);

        Map<Long, List<ProviderService>> byProvider = new LinkedHashMap<>();
        for (ProviderService service : services) {
            Provider provider = service.getProvider();

            if (provider.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
                continue;
            }
            if (filterByCity && !provider.getCity().equalsIgnoreCase(city.trim())) {
                continue;
            }
            if (savedProviderIds != null && !savedProviderIds.contains(provider.getId())) {
                continue;
            }
            if (wantedKeyword != null && !matchesKeyword(service, wantedKeyword)) {
                continue;
            }
            byProvider.computeIfAbsent(provider.getId(), key -> new ArrayList<>()).add(service); 
        }

        if (byProvider.isEmpty()) {
            return List.of();
        }

        if (wantedDay != null || wantedBlock != null) {
            byProvider.keySet().removeIf(providerId -> providerScheduleRepository
                .findAllByProviderIdAndIsAvailableTrue(providerId).stream()
                .noneMatch(hours -> (wantedDay == null || hours.getWeekday() == wantedDay)
                        && (wantedBlock == null || hours.overlaps(wantedBlock.start(), wantedBlock.end()))));
        }
        
        List<List<ProviderService>> groups = new ArrayList<>(byProvider.values());
        if (wantedSort == BrowseSort.NEWEST) {
            groups.sort(Comparator.comparing(CustomerService::newestCreatedAt, Comparator.reverseOrder()));
        } else {
            // no sort chosen: a fair default. The order changes once a day (same order all day, so paging and
            // testing stay stable) instead of always putting the oldest providers first.
            Collections.shuffle(groups, new Random(LocalDate.now(Appointment.TIME_ZONE).toEpochDay()));
        }

        return groups.stream().map(BrowseProviderDto::from).toList();
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
            return null;
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

    // MORNING, AFTERNOON or EVENING (see TimeBlock); null when no time of day was given
    private TimeBlock parseTimeOfDay(String timeOfDay) {
        if (timeOfDay == null || timeOfDay.isBlank()) {
            return null;
        }
        try {
            return TimeBlock.valueOf(timeOfDay.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown time of day. Use MORNING, AFTERNOON or EVENING.");
        }
    }

    private BrowseSort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return null;
        }
        String value = sort.trim().toUpperCase();
        if (value.equals("NEWEST")) {
            return BrowseSort.NEWEST;
        }

        if (value.equals("TOP_RATED") || value.equals("MOST_REVIEWED")) {
            throw new BadRequestException("Sorting by rating or number of reviews is not available yet.");
        }
        throw new BadRequestException("Unknown sort. Use NEWEST");
    }

    // a provider name match keeps all of that provider's services; otherwise the service itself must match
    private boolean matchesKeyword(ProviderService service, String keyword) {
        return containsIgnoreCase(service.getProvider().getName(), keyword)
                || containsIgnoreCase(service.getName(), keyword)
                || containsIgnoreCase(service.getDescription(), keyword);
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    private Set<Long> savedProviderIds(Long customerId) {
        if (customerId == null) {
            throw new BadRequestException("Customer id is required when saved only is true.");
        }
        return findCustomer(customerId).getSavedProviders().stream()
                .map(Provider::getId)
                .collect(Collectors.toSet());
    }

    private static OffsetDateTime newestCreatedAt(List<ProviderService> providerServices) {
        return providerServices.stream()
                .map(ProviderService::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }
}
