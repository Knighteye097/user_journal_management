package com.knighteye097.user_management_service.service;

import com.knighteye097.user_management_service.dto.UserRequest;
import com.knighteye097.user_management_service.dto.UserResponse;

import java.util.*;

/**
 * Service interface for managing users.
 */
public interface UserService {

    /**
     * Retrieves a list of all users.
     *
     * @return a list of {@code UserResponse} objects representing all users.
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email of the user to retrieve.
     * @return the {@code UserResponse} associated with the given email.
     * @throws RuntimeException if the user is not found.
     */
    UserResponse getUserByEmail(String email);

    /**
     * Updates the information of an existing user identified by their email.
     *
     * @param email       the email of the user to update.
     * @param updatedUser the new details for the user.
     * @return the updated {@code UserResponse} object.
     * @throws RuntimeException if the user is not found or the update fails.
     */
    UserResponse updateUserByEmail(String email, UserRequest updatedUser);

    /**
     * Deletes a user account based on the email address.
     *
     * @param email the email of the user to delete.
     * @throws RuntimeException if the user is not found or deletion fails.
     */
    void deleteUserByEmail(String email);
}