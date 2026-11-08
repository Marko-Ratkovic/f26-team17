package com.csc340.glowup.mvc;

import com.csc340.glowup.dto.*;
import com.csc340.glowup.enums.*;
import com.csc340.glowup.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping ("/customers/browse-services")
    public String browse(@RequestParam(required = false) String category,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String city,
                         @RequestParam(required = false) String day,
                         @RequestParam(required = false) String timeOfDay,
                         @RequestParam(required = false) String minRating,
                         @RequestParam(required = false) String sort,
                         @RequestParam(defaultValue = "false") boolean savedOnly,
                         HttpSession session,
                         HttpServletRequest request,
                         Model model) {
        Long customerId = UiSession.requireId(session, "customer");

        List<BrowseProviderDto> providers = customerService.browse(category, city, day, timeOfDay,
                keyword, savedOnly, customerId, sort, parseMinRating(minRating));
        
        // city drop-down lists the cities that have providers right now
        List<String> cities = customerService.browse(null, null, null, null, null, false, null, null, null).stream()
                .map(BrowseProviderDto::city)
                .distinct()
                .sorted()
                .toList();
        List<Long> savedIds = customerService.getSavedProviders(customerId).stream()
                .map(SavedProviderDto::id).toList();
        
        model.addAttribute("providers", providers);
        model.addAttribute("savedIds", savedIds);
        model.addAttribute("cities", cities);
        model.addAttribute("categoryOptions", categoryOptions());
        model.addAttribute("categoryLabels", categoryLabels());
        model.addAttribute("dayOptions", dayOptions());
        model.addAttribute("timeOptions", timeOptions());
        model.addAttribute("ratingOptions", ratingOptions());
        model.addAttribute("sortOptions", sortOptions());

        // what the customer picked, so the form shows it again
        model.addAttribute("selectedCategory", orEmpty(category));
        model.addAttribute("keyword", orEmpty(keyword));
        model.addAttribute("selectedCity", orEmpty(city));
        model.addAttribute("selectedDay", orEmpty(day));
        model.addAttribute("selectedTime", orEmpty(timeOfDay));
        model.addAttribute("selectedRating", orEmpty(minRating));
        model.addAttribute("selectedSort", orEmpty(sort));
        model.addAttribute("savedOnly", savedOnly);

        // save and remove buttons send the customer back to the same filtered list
        String query = request.getQueryString();
        model.addAttribute("returnTo", "/customers/browse-services" + (query == null ? "" : "?" + query));

        return "customer/browse-services";
    }

    @PostMapping("/customers/saved-providers/{providerId}/save")
    public String saveProvider(@PathVariable Long providerId,
                               @RequestParam(required = false) String returnTo,
                               HttpSession session) {
        customerService.saveProvider(UiSession.requireId(session, "customer"), providerId);
        return "redirect:" + safeReturnPath(returnTo);
    }

    @PostMapping("/customers/saved-providers/{providerId}/remove")
    public String removeSavedProvider(@PathVariable Long providerId,
                                      @RequestParam(required = false) String returnTo,
                                      HttpSession session) {
        customerService.removeSavedProvider(UiSession.requireId(session, "customer"), providerId);
        return "redirect:" + safeReturnPath(returnTo);
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

    private Double parseMinRating(String minRating) {
        if (minRating == null || minRating.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(minRating.trim());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Minimum rating must be a number.");
        }
    }

    private String orEmpty(String text) {
        return text == null ? "" : text;
    }

    private Map<String, String> option(String value, String label) {
        Map<String, String> option = new LinkedHashMap<>();
        option.put("value", value);
        option.put("label", label);
        return option;
    }

    private List<Map<String, String>> categoryOptions() {
        List<Map<String, String>> options = new ArrayList<>();
        for (ServiceCategory category : ServiceCategory.values()) {
            options.add(option(category.name(), categoryLabel(category)));
        }
        return options;
    }

    private Map<String, String> categoryLabels() {
        Map<String, String> labels = new LinkedHashMap<>();
        for (ServiceCategory category : ServiceCategory.values()) {
            labels.put(category.name(), categoryLabel(category));
        }
        return labels;
    }

    private String categoryLabel(ServiceCategory category) {
        return switch (category) {
            case HAIR_STYLING -> "Hair Styling";
            case NAIL_CARE -> "Nail Care";
            case MAKEUP_ARTISTRY -> "Makeup Artistry";
            case SKINCARE -> "Skincare";
            case BROWS_AND_LASHES -> "Brows & Lashes";
            case HAIR_REMOVAL -> "Hair Removal & Body Grooming";
        };
    }

    private List<Map<String, String>> dayOptions() {
        List<Map<String, String>> options = new ArrayList<>();
        for (Weekday day : Weekday.values()) {
            String name = day.name();
            options.add(option(name, name.charAt(0) + name.substring(1).toLowerCase()));
        }
        return options;
    }

    private List<Map<String, String>> timeOptions() {
        return List.of(
                option("MORNING", "Morning (9 AM-12 PM)"),
                option("AFTERNOON", "Afternoon (12-5 PM)"),
                option("EVENING", "Evening (5-9 PM)"));
    }

    private List<Map<String, String>> ratingOptions() {
        return List.of(
            option("4.5", "4.5+ stars"),
            option("4", "4.0+ stars"),
            option("3.5", "3.5+ stars"));
    }

    private List<Map<String, String>> sortOptions() {
        return List.of(
            option("", "Recommended"),
            option("TOP_RATED", "Top rated"),
            option("MOST_REVIEWED", "Most reviewed"),
            option("NEWEST", "Newest"));
    }

    // only paths inside the customer pages are allowed, so the redirect cannot leave the site
    private String safeReturnPath(String returnTo) {
        if (returnTo != null && returnTo.startsWith("/customers/")) {
            return returnTo;
        }
        return "/customers/browse-services";
    }
}
