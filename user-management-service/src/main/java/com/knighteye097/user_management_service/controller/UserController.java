package com.knighteye097.user_management_service.controller;


import com.knighteye097.user_management_service.dto.UserRequest;
import com.knighteye097.user_management_service.dto.UserResponse;
import com.knighteye097.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserByEmail(
            @PathVariable String email,
            @RequestBody UserRequest updatedUser
    ) {
        return ResponseEntity.ok(userService.updateUserByEmail(email, updatedUser));
    }

    @DeleteMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByEmail(
            @PathVariable String email
    ) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}