package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.AuthResponse;
import com.example.AddressBookApp.dto.LoginRequest;
import com.example.AddressBookApp.dto.RegisterRequest;
import com.example.AddressBookApp.model.User;
import com.example.AddressBookApp.repository.UserRepository;
import com.example.AddressBookApp.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String register(RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return "Email already in use!";
            }

            User newUser = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            userRepository.save(newUser);
            return "User registered successfully!";
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            System.out.println("Login request received for email: " + request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User found: " + user.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            String token = jwtUtil.generateToken(user.getEmail());
            System.out.println("Token generated: " + token);

            return new AuthResponse(token);
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return new AuthResponse("Login failed: " + e.getMessage());
        }
    }
}




