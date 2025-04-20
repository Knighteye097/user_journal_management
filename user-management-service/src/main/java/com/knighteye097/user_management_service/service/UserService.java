package com.knighteye097.user_management_service.service;

import com.knighteye097.user_management_service.dto.UserEvent;
import com.knighteye097.user_management_service.dto.UserResponse;
import com.knighteye097.user_management_service.entity.User;
import com.knighteye097.user_management_service.kafka.UserEventProducer;
import com.knighteye097.user_management_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        userEventProducer.sendEvent(new UserEvent(
                "FETCH_ALL_USERS",
                "system",
                "system",
                "Fetched all users",
                LocalDateTime.now()
        ));

        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userEventProducer.sendEvent(new UserEvent(
                "FETCH_USER_BY_ID",
                user.getEmail(),
                user.getName(),
                "Fetched user with ID " + id,
                LocalDateTime.now()
        ));

        return toResponse(user);
    }

    public UserResponse updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setRoles(updatedUser.getRoles());

        User saved = userRepository.save(user);

        userEventProducer.sendEvent(new UserEvent(
                "USER_UPDATED",
                saved.getEmail(),
                saved.getName(),
                "User updated: " + saved.getName(),
                LocalDateTime.now()
        ));

        return toResponse(saved);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.deleteById(id);

        userEventProducer.sendEvent(new UserEvent(
                "USER_DELETED",
                user.getEmail(),
                user.getName(),
                "User deleted with ID " + id,
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