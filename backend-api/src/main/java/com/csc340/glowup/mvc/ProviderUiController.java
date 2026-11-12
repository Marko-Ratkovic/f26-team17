package com.csc340.glowup.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.dto.ProviderScheduleResponse;
import com.csc340.glowup.dto.ProviderUpdateRequest;
import com.csc340.glowup.dto.ProviderUpdateScheduleRequest;
import com.csc340.glowup.dto.AppointmentReasonRequest;
import com.csc340.glowup.entity.ProviderService;
import com.csc340.glowup.service.AppointmentService;
import com.csc340.glowup.service.ConflictException;
import com.csc340.glowup.service.ProviderProfileService;
import com.csc340.glowup.service.ProviderScheduleService;
import com.csc340.glowup.service.ReviewService;
import com.csc340.glowup.service.ServiceCatalogService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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

    @GetMapping("/{providerId}/image")
    public String getImg(HttpSession session){
        Long providerId = UiSession.requireId(session, "provider");
        ProviderResponse provider = providerProfileService.getProfile(providerId);
        if(provider.imagePath()!=null && !provider.imagePath().isEmpty()){
            return "redirect:/images/providers/"+provider.imagePath();
        }
        return "redirect:/images/provider-placeholder.svg";
    }

    @GetMapping("/edit-details")
    public String editDetals(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("provider", providerProfileService.getProfile(providerId));
        return "/provider/edit-details";
    }

    @PostMapping("/update-details")
    public String updateDetails(HttpSession session, ProviderUpdateRequest request, MultipartFile imageFile,RedirectAttributes redirect){
        Long providerId = UiSession.requireId(session, "provider");
        providerProfileService.updateProfile(providerId, request);
        if(imageFile != null && !imageFile.isEmpty()){
            providerProfileService.saveImg(providerId,imageFile);
        }
        redirect.addFlashAttribute("success", "Your profile have been updated.");
        return "redirect:/providers/profile";
    }

    @GetMapping("/manage-availability")
    public String manageAvailability(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("providerSchedules",providerScheduleService.listForProvider(providerId));
        return "/provider/manage-availability";
    }

    @PostMapping("/update-availability")
    public String updateAvailability(HttpSession session,
        @Valid ProviderUpdateScheduleRequest providerUpdateScheduleRequest,RedirectAttributes redirect){
        Long providerId = UiSession.requireId(session,"provider" );
        providerScheduleService.updateWeek(providerId, providerUpdateScheduleRequest);
        redirect.addFlashAttribute("success", "Your availability hours have been updated.");
        return "redirect:/providers/profile";
    }

    @GetMapping("/manage-appointments")
    public String manageAppointments(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session, "provider");
        model.addAttribute("appointments",appointmentService.listForProvider(providerId));
        return "/provider/manage-appointments";
    }

    @GetMapping("/appointment/{appointmentId}/details")
    public String appointmentDetails(@PathVariable Long appointmentId,HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("appointment", appointmentService.getDetail(providerId, appointmentId));
        return "/provider/appointment-details";
    }

    @PostMapping("/appointment/{appointmentId}/confirm")
    public String confirmAppointment(@PathVariable Long appointmentId,HttpSession session,RedirectAttributes redirect){
        Long providerId = UiSession.requireId(session,"provider" );
        try{
            appointmentService.confirm(providerId, appointmentId);
            redirect.addFlashAttribute("success", "Appointment confirmed successfully.");
        } catch (ConflictException e){
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/providers/manage-appointments";
    }

    @PostMapping("/appointment/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable Long appointmentId,String reason, HttpSession session,RedirectAttributes redirect){
        Long providerId = UiSession.requireId(session,"provider" );
        appointmentService.cancelByProvider(providerId,appointmentId,reason);
        redirect.addFlashAttribute("success", "Appointment cancelled successfully.");
        return "redirect:/providers/manage-appointments";
    }

    @PostMapping("/appointment/{appointmentId}/decline")
    public String declineAppointment(@PathVariable Long appointmentId,String reason, HttpSession session,RedirectAttributes redirect){
        Long providerId = UiSession.requireId(session,"provider" );
        appointmentService.decline(providerId,appointmentId,reason);
        redirect.addFlashAttribute("success", "Appointment declined successfully.");
        return "redirect:/providers/manage-appointments";
    }

    @PostMapping("/appointment/{appointmentId}/complete")
    public String markComplete(@PathVariable Long appointmentId,String reason, HttpSession session,RedirectAttributes redirect){
        Long providerId = UiSession.requireId(session,"provider" );
        try{
            appointmentService.markCompleted(providerId,appointmentId);
            redirect.addFlashAttribute("success", "Appointment mark completed successfully.");
        } catch (ConflictException e){
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/providers/manage-appointments";
    }

    @GetMapping("/manage-services")
    public String manageServices(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("services", catalogService.listServices(providerId));
        model.addAttribute("provider",providerProfileService.getProfile(providerId));
        return "/provider/manage-services";
    }

    @GetMapping("/new-service")
    public String newService(HttpSession session, Model model){
        Long providerId = UiSession.requireId(session,"provider" );
        model.addAttribute("provider",providerProfileService.getProfile(providerId));
        return "/provider/new-service";
    }

    private void addDashboardData(Long providerId, Model model){
        ProviderResponse provider = providerProfileService.getProfile(providerId);
        model.addAttribute("provider",provider);
        model.addAttribute("appointments", appointmentService.listForProvider(providerId));
        model.addAttribute("services", catalogService.listServices(providerId));
        // model.addAttribute("reviews",reviewService.listForProvider(providerId));
    }
}

