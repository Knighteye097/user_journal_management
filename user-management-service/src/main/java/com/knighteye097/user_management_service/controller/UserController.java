package com.knighteye097.user_management_service.controller;


import com.knighteye097.user_management_service.dto.UserRequest;
import com.knighteye097.user_management_service.dto.UserResponse;
import com.knighteye097.user_management_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "List of users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get user by email", description = "Retrieve a user by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(hidden = true)))
    })
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "Email of the user to be retrieved", required = true) @PathVariable String email
    ) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user by email", description = "Update an existing user's information by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(hidden = true)))
    })
    public ResponseEntity<UserResponse> updateUserByEmail(
            @Parameter(description = "Email of the user to be updated", required = true) @PathVariable String email,
            @Valid @RequestBody UserRequest updatedUser
    ) {
        return ResponseEntity.ok(userService.updateUserByEmail(email, updatedUser));
    }

    @DeleteMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user by email", description = "Delete a user by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Void> deleteUserByEmail(
            @Parameter(description = "Email of the user to be deleted", required = true) @PathVariable String email
    ) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}