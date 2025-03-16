package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.AuthResponse;
import com.example.AddressBookApp.dto.LoginRequest;
import com.example.AddressBookApp.dto.RegisterRequest;
import com.example.AddressBookApp.dto.ResetPasswordRequest;
import com.example.AddressBookApp.model.PasswordResetToken;
import com.example.AddressBookApp.model.User;
import com.example.AddressBookApp.repository.PasswordResetTokenRepository;
import com.example.AddressBookApp.repository.UserRepository;
import com.example.AddressBookApp.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Override
    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already in use!";
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(newUser);

        // Send confirmation email
        boolean emailSent = emailService.sendEmail(
                request.getEmail(),
                "Registration Successful",
                "Welcome to AddressBookApp! Your account has been created successfully."
        );

        if (!emailSent) {
            return "User registered, but email sending failed!";
        }

        return "User registered successfully! Email sent.";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Reset Your Password",
                "Click the link to reset your password: " + resetLink);

        return "Password reset email sent!";
    }

    public String resetPassword(String token, ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token expired!";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.deleteByToken(token);  // Delete the used token
        return "Password reset successful!";
    }
}





