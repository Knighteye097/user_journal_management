package com.knighteye097.user_management_service.service;

import com.knighteye097.user_management_service.dto.AuthResponse;
import com.knighteye097.user_management_service.dto.LoginRequest;
import com.knighteye097.user_management_service.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 * Provides methods to register a new user and authenticate an existing user.
 */
public interface AuthService {

    /**
     * Registers a new user with the provided registration details.
     *
     * @param request the registration details required to create a new user.
     * @return an {@code AuthResponse} containing the generated JWT token.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates an existing user using the provided login credentials.
     *
     * @param request the login credentials containing email and password.
     * @return an {@code AuthResponse} containing the generated JWT token.
     * @throws RuntimeException if authentication fails due to invalid credentials.
     */
    AuthResponse login(LoginRequest request);
}