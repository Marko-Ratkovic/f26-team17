package com.csc340.glowup.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.csc340.glowup.dto.ProviderRegistrationRequest;
import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.Weekday;
import com.csc340.glowup.dto.CustomerResponse;
import com.csc340.glowup.service.ProviderProfileService;
import com.csc340.glowup.service.BadRequestException;
import com.csc340.glowup.service.ConflictException;
import com.csc340.glowup.service.CustomerService;
import com.csc340.glowup.service.ProviderProfileService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AppController {

    private final ProviderProfileService providerProfileService;
    private final CustomerService customerService;

    public AppController(ProviderProfileService providerProfileService, CustomerService customerService){
        this.providerProfileService = providerProfileService;
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String home(Model model){
        return "public/index";
    }

    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("weekdays", Weekday.values());
        return "public/register";
    }

    @PostMapping("/login")
    public String authenticate(
            @RequestParam  String loginRole,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {
            if ("provider".equalsIgnoreCase(loginRole)) {
            ProviderResponse provider = providerProfileService.authenticate(email,password);
            session.setAttribute("userId", provider.id());
            session.setAttribute("userRole", "provider");
            session.setAttribute("userName", provider.name());
            return "redirect:/providers/dashboard";
            }
            CustomerResponse customer = customerService.authenticate(email, password);
            setSession(session, customer.id(), "customer", customer.name());
            return "redirect:/customers/profile";
        } catch (BadRequestException e){
            model.addAttribute("error", e.getMessage());
            return "public/login";
        }
    }

    @PostMapping("/provider/register")
    public String createProvider(
            @Valid ProviderRegistrationRequest providerRegistrationRequest,
            HttpSession session,Model model){
        String error;
        try {        
            ProviderResponse provider = providerProfileService.register(providerRegistrationRequest);
            setSession(session, provider.id(), "provider", provider.name());
            return "redirect:/providers/dashboard";
        } catch (ConflictException e){
            model.addAttribute("error",e.getMessage());
            return "public/register";
        }

    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private void setSession(HttpSession session, Long id, String role, String name) {
        session.setAttribute("userId", id);
        session.setAttribute("userRole", role);
        session.setAttribute("userName", name);
    }
}
