package com.nexttech.backend.service;

import com.nexttech.backend.dto.*;
import com.nexttech.backend.exception.AppExceptions;
import com.nexttech.backend.model.User;
import com.nexttech.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    private final Map<String, OtpEntry> otpCache = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String email) {
        String normalizedEmail = normalize(email);
        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        LocalDateTime expiresAt = LocalDateTime.now().plus(OTP_TTL);

        otpCache.put(normalizedEmail, new OtpEntry(otp, expiresAt));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(normalizedEmail);
        message.setSubject("NEXT TECH Technology pvt ltd : ");
        message.setText("Your one-time password is: " + otp + ". It will expire in " + OTP_TTL.toMinutes() + " minutes.");
        mailSender.send(message);
    }

    public VerifyOTPResponse verifyOtp(String email, String otpCode) {
        String normalizedEmail = normalize(email);
        OtpEntry entry = otpCache.get(normalizedEmail);

        if (entry == null) {
            throw new AppExceptions.InvalidOtpException("OTP is invalid");
        }
        if (entry.expiresAt().isBefore(LocalDateTime.now())) {
            otpCache.remove(normalizedEmail);
            throw new AppExceptions.OtpExpiredException("OTP has expired");
        }
        if (!entry.code().equals(otpCode)) {
            throw new AppExceptions.InvalidOtpException("OTP is invalid");
        }

        otpCache.remove(normalizedEmail);
        User user = userRepository.findByEmail(normalizedEmail);
        boolean userAlreadyExists = user != null;
        String jwtToken = userAlreadyExists ? jwtService.generateToken(user) : null;

        return VerifyOTPResponse.builder()
                .email(normalizedEmail)
                .isEmailVerified(true)
                .userAlreadyExists(userAlreadyExists)
                .token(jwtToken)
                .build();
    }

    public AuthResponse register(AuthRequest request) {
        String username = request.getUsername().trim();
        String email = normalize(request.getEmail());

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            throw new AppExceptions.UserAlreadyExistsException("User already exists");
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
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
        String username = request.getUsername().trim();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new AppExceptions.InvalidCredentialsException("Invalid username or password");
        } catch (AuthenticationException ex) {
            throw new AppExceptions.InvalidCredentialsException("Authentication failed");
        }

        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppExceptions.InvalidCredentialsException("Invalid username or password")
        );
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

    private String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private record OtpEntry(String code, LocalDateTime expiresAt) {}
}
