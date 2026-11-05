package com.csc340.glowup.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.csc340.glowup.service.ProviderProfileService;

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
}
