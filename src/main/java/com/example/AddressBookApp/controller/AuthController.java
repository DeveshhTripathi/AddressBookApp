package com.example.AddressBookApp.controller;

import com.example.AddressBookApp.dto.AuthResponse;
import com.example.AddressBookApp.dto.LoginRequest;
import com.example.AddressBookApp.dto.RegisterRequest;
import com.example.AddressBookApp.dto.ResetPasswordRequest;
import com.example.AddressBookApp.service.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(token, request);
    }
}

