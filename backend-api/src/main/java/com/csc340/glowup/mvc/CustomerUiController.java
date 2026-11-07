package com.csc340.glowup.mvc;

import com.csc340.glowup.dto.*;
import com.csc340.glowup.enums.Weekday;
import com.csc340.glowup.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CustomerUiController {

    private final CustomerService customerService;
    private final AppointmentService appointmentService;
    private final Validator validator;

    public CustomerUiController(CustomerService customerService,
                                AppointmentService appointmentService,
                                Validator validator) {
        this.customerService = customerService;
        this.appointmentService = appointmentService;
        this.validator = validator;
    }

    /* 
     * The register form posts to /register until the role toggle runs, then to /{role}/register,
     * so both paths are accepted for customer.
     * When something is wrong the form is shown again with what the customer typed, except the
     * field that caused the error (and the confirm password when it is no longer valid).
    */ 
    @PostMapping({"/register", "/customer/register"})
    public String register(@RequestParam("name") String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam("confirm-password") String confirmPassword,
                           @RequestParam(name = "customer-phone", required = false) String phoneNumber,
                           HttpSession session,
                           Model model) {
        String error;
        Set<String> clear = new HashSet<>();

        if (!password.equals(confirmPassword)) {
            error = "Passwords do not match.";
            clear.add("confirmPassword");
        } else {
            CustomerRegisterRequest request = new CustomerRegisterRequest(name, email, password, phoneNumber);
            Set<ConstraintViolation<CustomerRegisterRequest>> violations = validator.validate(request);

            if (!violations.isEmpty()) {
                error = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .sorted()
                        .collect(Collectors.joining("; "));
                violations.forEach(violation -> clear.add(violation.getPropertyPath().toString()));
            } else {
                try {
                    CustomerResponse customer = customerService.register(request);
                    session.setAttribute("userId", customer.id());
                    session.setAttribute("userRole", "customer");
                    session.setAttribute("userName", customer.name());
                    return "redirect:/customers/profile";
                } catch (ConflictException e) {
                    error = e.getMessage();
                    clear.add("email");
                }
            }
        }

        // a new password makes the old confirmation meaningless
        if (clear.contains("password")) {
            clear.add("confirmPassword");
        }

        model.addAttribute("error", error);
        model.addAttribute("weekdays", Weekday.values());
        model.addAttribute("formName", clear.contains("name") ? "" : name);
        model.addAttribute("formEmail", clear.contains("email") ? "" : email);
        model.addAttribute("formPhone", clear.contains("phoneNumber") || phoneNumber == null ? "" : phoneNumber);
        model.addAttribute("formPassword", clear.contains("password") ? "" : password);
        model.addAttribute("formConfirmPassword", clear.contains("confirmPassword") ? "" : confirmPassword);
        return "public/register";
    }

    @GetMapping ("/customers/profile")
    public String profile(HttpSession session, Model model) {
        Long customerId = UiSession.requireId(session, "customer");
        CustomerResponse customer = customerService.getById(customerId);
        SafetyNotesDto notes = customer.safetyNotes();

        model.addAttribute("customer", customer);
        model.addAttribute("allergyLabel", allergyLabel(notes));
        model.addAttribute("allergyDetails", notes == null || isBlank(notes.allergyDetails())
                ? "None listed." : notes.allergyDetails());
        model.addAttribute("sensitivities", notes == null || isBlank(notes.sensitivities())
                ? "None listed." : notes.sensitivities());
        model.addAttribute("savedProviders", customerService.getSavedProviders(customerId));
        model.addAttribute("bookedProviders", bookedProviders(appointmentService.listForCustomer(customerId)));
        return "customer/profile";
    }


    // Helper Methods
    private String allergyLabel(SafetyNotesDto notes) {
        if (notes == null || notes.allergyStatus() == null) {
            return "Not provided.";
        }
        return switch (notes.allergyStatus()) {
            case KNOWN -> "Known allergies listed.";
            case NO_KNOWN -> "No known allergies.";
            case NOT_PROVIDED -> "Not provided.";
        };
    }

    // one row per provider the customer has booked: name, number of appointments, number completed
    private List<Map<String, Object>> bookedProviders(List<AppointmentResponse> appointments) {
        Map<Long, List<AppointmentResponse>> byProvider = appointments.stream()
                .collect(Collectors.groupingBy(AppointmentResponse::providerId, LinkedHashMap::new,
                Collectors.toList()));
        
        List<Map<String, Object>> rows = new ArrayList<>();
        byProvider.forEach((providerId, list) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", list.get(0).providerName());
            row.put("total", list.size());
            row.put("completed", list.stream().filter(AppointmentResponse::completed).count());
            rows.add(row);
        });
        return rows;
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
