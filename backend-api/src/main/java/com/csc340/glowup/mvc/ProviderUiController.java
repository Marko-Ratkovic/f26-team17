package com.csc340.glowup.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.dto.ProviderScheduleResponse;
import com.csc340.glowup.service.AppointmentService;
import com.csc340.glowup.service.ProviderProfileService;
import com.csc340.glowup.service.ProviderScheduleService;
import com.csc340.glowup.service.ReviewService;
import com.csc340.glowup.service.ServiceCatalogService;

import jakarta.servlet.http.HttpSession;

@Controller 
@RequestMapping("/providers")
public class ProviderUiController {
    private final ProviderProfileService providerProfileService;
    private final ServiceCatalogService catalogService;
    private final ProviderScheduleService providerScheduleService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    public ProviderUiController(
            ProviderProfileService providerService,
            ServiceCatalogService catalogService,
            ProviderScheduleService providerScheduleService,
            AppointmentService appointmentService,
            ReviewService reviewService){
        this.providerProfileService = providerService;
        this.catalogService = catalogService;
        this.providerScheduleService = providerScheduleService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider");
        addDashboardData(providerId,model);
        return "/provider/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        ProviderResponse provider = providerProfileService.getProfile(providerId);
        List<ProviderScheduleResponse> providerSchedules = providerScheduleService.listForProvider(providerId);
        model.addAttribute("provider",provider);
        model.addAttribute("services", catalogService.listServices(providerId));
        model.addAttribute("providerSchedules",providerSchedules);
        return "/provider/profile";
    }

    @GetMapping("/edit-details")
    public String editDetals(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("provider", providerProfileService.getProfile(providerId));
        return "/provider/edit-details";
    }

    @GetMapping("/manage-availability")
    public String manageAvailability(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("providerSchedules",providerScheduleService.listForProvider(providerId));
        return "/provider/manage-availability";
    }

    private void addDashboardData(Long providerId, Model model){
        ProviderResponse provider = providerProfileService.getProfile(providerId);
        model.addAttribute("provider",provider);
        model.addAttribute("appointments", appointmentService.listForProvider(providerId));
        model.addAttribute("services", catalogService.listServices(providerId));
        // model.addAttribute("reviews",reviewService.listForProvider(providerId));
    }
}

