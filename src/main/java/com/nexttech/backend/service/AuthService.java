package com.nexttech.backend.service;

import com.nexttech.backend.dto.*;
import com.nexttech.backend.exception.AppExceptions;
import com.nexttech.backend.model.User;
import com.nexttech.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender; // You'll need to configure this in application.properties

    // Temporary storage for OTPs (Key: Email, Value: OTP)
    // In production, use Redis with an expiration time!
    private final Map<String, String> otpCache = new HashMap<>();

    public void generateAndSendOtp(String email) {
        // 1. Generate a 6-digit random code
        String otp = String.format("%06d", new Random().nextInt(999999));

        // 2. Save to cache
        otpCache.put(email, otp);

        // 3. Send via Email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("NEXT TECH Technology pvt ltd : ");
        message.setText("Your one-time password is: " + otp + ". It will expire in 5 minutes.");
        mailSender.send(message);
    }

    public VerifyOTPResponse verifyOtp(String email, String otpCode) {

        String cachedOtp = otpCache.get(email);
        boolean isEmailVerified = cachedOtp != null && cachedOtp.equals(otpCode);

        User user = userRepository.findByEmail(email);
        boolean userAlreadyExists = user != null && user.getEmail().equals(email);

        otpCache.remove(email);
        return VerifyOTPResponse.builder()
                .email(email)
                .isEmailVerified(isEmailVerified)
                .userAlreadyExists(userAlreadyExists)
                .build();
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new AppExceptions.UserAlreadyExistsException("User already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mobileNumber(request.getMobileNumber())
                .loginDeviceId(request.getLoginDeviceId())
                .loginDeviceOs(request.getLoginDeviceOs())
                .lastLoginAt(LocalDateTime.now())
                .password(passwordEncoder.encode(request.getPassword()))
                .isEmailVerified(request.isEmailVerified())
                .isActive(true)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }
}