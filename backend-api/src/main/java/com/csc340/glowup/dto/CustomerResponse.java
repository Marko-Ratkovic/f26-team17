package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Customer;

import java.util.List;

public record CustomerResponse(
        Long id, 
        String name, 
        String email,
        String phoneNumber,
        SafetyNotesDto safetyNotes,
        List<PreferredTimeRangeDto> preferredTimeRanges) { 
    
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getUserAccount().getEmail(),
                customer.getUserAccount().getPhoneNumber(),
                SafetyNotesDto.from(customer.getSafetyNotes()),
                customer.getPreferredTimeRanges().stream().map(PreferredTimeRangeDto::from).toList());
    }
}
