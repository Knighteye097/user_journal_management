package com.knighteye097.user_management_service.service;

import com.knighteye097.user_management_service.dto.*;
import com.knighteye097.user_management_service.entity.*;
import com.knighteye097.user_management_service.kafka.UserEventProducer;
import com.knighteye097.user_management_service.repository.UserRepository;
import com.knighteye097.user_management_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserEventProducer eventProducer;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles().stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet()))
                .build();

        userRepository.save(user);

        // TODO: publish event to Kafka here
        eventProducer.sendUserCreatedEvent(user);

        String jwtToken = jwtService.generateToken(user.getEmail(), user.getRoles());
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(user.getEmail(), user.getRoles());
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}