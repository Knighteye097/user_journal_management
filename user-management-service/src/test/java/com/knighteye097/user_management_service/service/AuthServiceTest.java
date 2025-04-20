package com.knighteye097.user_management_service.service;

import com.knighteye097.user_management_service.dto.AuthResponse;
import com.knighteye097.user_management_service.dto.LoginRequest;
import com.knighteye097.user_management_service.dto.RegisterRequest;
import com.knighteye097.user_management_service.entity.Role;
import com.knighteye097.user_management_service.entity.User;
import com.knighteye097.user_management_service.kafka.UserEventProducer;
import com.knighteye097.user_management_service.repository.UserRepository;
import com.knighteye097.user_management_service.security.JwtService;
import com.knighteye097.user_management_service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthService Tests")
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserEventProducer eventProducer;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Successful Registration")
        public void testRegister_Success() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("password123")
                    .roles(Set.of("ROLE_USER"))
                    .build();

            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(jwtService.generateToken(eq(request.getEmail()), any())).thenReturn("jwt-token");

            doAnswer(invocation -> invocation.getArgument(0))
                    .when(userRepository).save(any(User.class));

            // Act & Assert
            AuthResponse response = authService.register(request);
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            verify(userRepository, times(1)).save(any(User.class));
            verify(eventProducer, times(1)).sendEvent(any());
            verify(jwtService, times(1)).generateToken(eq(request.getEmail()), any());
        }

        @Test
        @DisplayName("Registration Failure - Exception Thrown")
        public void testRegister_Failure() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("password123")
                    .roles(Set.of("ROLE_USER"))
                    .build();

            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
            assertEquals("DB error", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Successful Login")
        public void testLogin_Success() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            Authentication dummyAuth = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(dummyAuth);

            User user = User.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("encodedPassword")
                    .roles(Collections.singleton(Role.ROLE_USER))
                    .build();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(jwtService.generateToken(eq(user.getEmail()), any())).thenReturn("jwt-token");

            // Act & Assert
            AuthResponse response = authService.login(request);
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());

            verify(authenticationManager, times(1))
                    .authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository, times(1)).findByEmail(request.getEmail());
            verify(eventProducer, times(1)).sendEvent(any());
            verify(jwtService, times(1)).generateToken(eq(user.getEmail()), any());
        }

        @Test
        @DisplayName("Login Failure - Invalid Credentials")
        public void testLogin_InvalidCredentials() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("test@example.com")
                    .password("wrongPassword")
                    .build();

            doThrow(new BadCredentialsException("Invalid credentials"))
                    .when(authenticationManager)
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> authService.login(request));
            verify(authenticationManager, times(1))
                    .authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository, never()).findByEmail(any());
        }
    }
}