package com.knighteye097.user_management_service.service.impl;

import com.knighteye097.user_management_service.dto.UserEvent;
import com.knighteye097.user_management_service.dto.UserRequest;
import com.knighteye097.user_management_service.dto.UserResponse;
import com.knighteye097.user_management_service.entity.EventType;
import com.knighteye097.user_management_service.entity.Role;
import com.knighteye097.user_management_service.entity.User;
import com.knighteye097.user_management_service.kafka.UserEventProducer;
import com.knighteye097.user_management_service.repository.UserRepository;
import com.knighteye097.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        userEventProducer.sendEvent(new UserEvent(
                EventType.ALL_USER_FETCHED,
                "system",
                "system",
                "Fetched all users",
                LocalDateTime.now()
        ));

        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userEventProducer.sendEvent(new UserEvent(
                EventType.USER_FETCHED_BY_ID,
                user.getEmail(),
                user.getName(),
                "Fetched user with Mail_Id " + email,
                LocalDateTime.now()
        ));

        return toResponse(user);
    }

    public UserResponse updateUserByEmail(String email, UserRequest updatedUserRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUserRequest.getName());
        user.setPassword(passwordEncoder.encode(updatedUserRequest.getPassword()));
        user.setRoles(updatedUserRequest
                        .getRoles()
                        .stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet()));

        User saved = userRepository.save(user);

        userEventProducer.sendEvent(new UserEvent(
                EventType.USER_UPDATED,
                saved.getEmail(),
                saved.getName(),
                "User updated: " + saved.getName(),
                LocalDateTime.now()
        ));

        return toResponse(saved);
    }

    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.deleteById(user.getId());

        userEventProducer.sendEvent(new UserEvent(
                EventType.USER_DELETED,
                user.getEmail(),
                user.getName(),
                "User deleted with Mail_Id " + email,
                LocalDateTime.now()
        ));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
}
