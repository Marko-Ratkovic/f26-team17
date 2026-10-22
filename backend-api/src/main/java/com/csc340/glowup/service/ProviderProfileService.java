package com.csc340.glowup.service;

import com.csc340.glowup.entity.Provider;
import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.repository.ProviderRepository;
import com.csc340.glowup.repository.UserAccountRepository;
import org.springframework.transaction.annotation.Transactional;

import com.csc340.glowup.dto.ProviderRegistrationRequest;
import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.dto.ProviderUpdateRequest;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ProviderProfileService {
    
    private final ProviderRepository providerRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProviderScheduleService providerScheduleService;

    public ProviderProfileService(
        ProviderRepository providerRepository,
        UserAccountRepository userAccountRepository,
        PasswordEncoder passwordEncoder,
        ProviderScheduleService providerScheduleService){
        this.providerRepository = providerRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.providerScheduleService = providerScheduleService;
    }

    public List<Provider> findAll(){
        return providerRepository.findAll();
    }

    public ProviderResponse register(ProviderRegistrationRequest request){
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        ensureEmailAvailable(email);
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must be no more than 72 UTF-8 bytes.");
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(email);
        userAccount.setPassword(passwordEncoder.encode(request.password()));
        userAccount.setAccountRole(AccountRole.PROVIDER);
        userAccount.setPhoneNumber(normalizePhone(request.phoneNumber()));
        userAccount.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        UserAccount savedUser = userAccountRepository.save(userAccount);

        Provider provider = new Provider();
        provider.setUserAccount(savedUser);
        provider.setName(request.name().trim());
        provider.setBio(request.bio());
        provider.setCertifications(request.certifications());
        provider.setYearsOfExperience(request.yearsOfExperience());
        provider.setImagePath(request.imagePath());
        provider.setAddressLine(request.addressLine());        
        provider.setCity(request.city());    
        provider.setState(request.state());    
        provider.setZipcode(request.zipcode()); 
        Provider saved = providerRepository.save(provider);
        providerScheduleService.createWeek(saved, request.availableDays(), request.openTime(), request.closeTime());
        return ProviderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProviderResponse getPublicProfile(Long providerId){
        Provider provider = findProvider(providerId);
        if (provider.getUserAccount().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new NotFoundException("Provider " + providerId + " was not found.");
        }
        return ProviderResponse.from(provider);
    }

    @Transactional(readOnly = true)
    public ProviderResponse getProfile(Long providerId){
        return ProviderResponse.from(findProvider(providerId));
    }

    Provider findProvider(Long providerId){
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider " + providerId + " was not found."));
    }

    public ProviderResponse updateProfile(Long provideId, ProviderUpdateRequest request){
        Provider provider = findProvider(provideId);
        provider.setName(request.name().trim());
        provider.setBio(request.bio());
        provider.setCertifications(request.certifications());
        if (request.yearsOfExperience() != null) {
            provider.setYearsOfExperience(request.yearsOfExperience());
        }
        provider.setAddressLine(request.addressLine());        
        provider.setCity(request.city());    
        provider.setState(request.state());    
        provider.setZipcode(request.zipcode());    
        return ProviderResponse.from(providerRepository.save(provider));
    }

    private static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String digits = phone.replaceAll("[^0-9+]", "");
        return digits.isEmpty() ? null : digits;
    }

    private void ensureEmailAvailable(String email) {
        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with that email already exists.");
        }
    }


}
