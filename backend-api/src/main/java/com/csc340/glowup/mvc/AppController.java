package com.csc340.glowup.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.csc340.glowup.dto.ProviderResponse;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.service.ProviderProfileService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AppController {

    private final ProviderProfileService providerProfileService;

    public AppController(ProviderProfileService providerProfileService){
        this.providerProfileService = providerProfileService;
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
    public String register() {
        return "public/register";
    }

    @PostMapping("/login")
    public String authenticate(
            @RequestParam  String role,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session){
        if("provider".equalsIgnoreCase(role)){
            ProviderResponse provider = providerProfileService.authenticate(email,password);
            session.setAttribute("userId", provider.id());
            session.setAttribute("userRole", "provider");
            session.setAttribute("userName", provider.name());
            return "redirect:/providers/dashboard";
        }
        return "redirect:/";
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private void setSession(HttpSession session, Long id, AccountRole role, String name) {
        session.setAttribute("userId", id);
        session.setAttribute("userRole", role);
        session.setAttribute("userName", name);
    }
}
