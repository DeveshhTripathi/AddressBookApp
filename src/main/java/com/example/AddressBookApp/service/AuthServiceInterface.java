package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.AuthResponse;
import com.example.AddressBookApp.dto.LoginRequest;
import com.example.AddressBookApp.dto.RegisterRequest;

public interface AuthServiceInterface {
    String register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
