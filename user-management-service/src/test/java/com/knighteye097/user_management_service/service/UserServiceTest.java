package com.knighteye097.user_management_service.service;

import com.knighteye097.user_management_service.dto.UserEvent;
import com.knighteye097.user_management_service.dto.UserRequest;
import com.knighteye097.user_management_service.dto.UserResponse;
import com.knighteye097.user_management_service.entity.Role;
import com.knighteye097.user_management_service.entity.User;
import com.knighteye097.user_management_service.kafka.UserEventProducer;
import com.knighteye097.user_management_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.knighteye097.user_management_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserService Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users successfully")
        void shouldReturnAllUsersSuccessfully() {
            // Arrange
            List<User> users = Arrays.asList(
                    User.builder().id(1L).name("Test User 1").email("test1@example.com").roles(Set.of(Role.ROLE_USER)).build(),
                    User.builder().id(2L).name("Test User 2").email("test2@example.com").roles(Set.of(Role.ROLE_ADMIN)).build()
            );
            when(userRepository.findAll()).thenReturn(users);

            // Act
            List<UserResponse> userResponses = userService.getAllUsers();

            // Assert
            assertEquals(2, userResponses.size());
            assertEquals("Test User 1", userResponses.get(0).getName());
            assertEquals("Test User 2", userResponses.get(1).getName());

            verify(userRepository, times(1)).findAll();
            verify(userEventProducer, times(1)).sendEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Should return an empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<UserResponse> userResponses = userService.getAllUsers();

            // Assert
            assertTrue(userResponses.isEmpty());
            verify(userRepository, times(1)).findAll();
            verify(userEventProducer, times(1)).sendEvent(any(UserEvent.class));
        }
    }

    @Nested
    @DisplayName("getUserByEmail Tests")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Should return user when email exists")
        void shouldReturnUserWhenEmailExists() {
            // Arrange
            String email = "test@example.com";
            User user = User.builder().id(1L).name("Test User").email(email).roles(Set.of(Role.ROLE_USER)).build();
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act
            UserResponse userResponse = userService.getUserByEmail(email);

            // Assert
            assertNotNull(userResponse);
            assertEquals("Test User", userResponse.getName());
            assertEquals(email, userResponse.getEmail());

            verify(userRepository, times(1)).findByEmail(email);
            verify(userEventProducer, times(1)).sendEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when email does not exist")
        void shouldThrowExceptionWhenEmailDoesNotExist() {
            // Arrange
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.getUserByEmail(email));

            verify(userRepository, times(1)).findByEmail(email);
            verify(userEventProducer, never()).sendEvent(any(UserEvent.class));
        }
    }

    @Nested
    @DisplayName("updateUserByEmail Tests")
    class UpdateUserByEmailTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Arrange
            String email = "test@example.com";
            User existingUser = User.builder().id(1L).name("Old Name").email(email).password("oldPassword").roles(Set.of(Role.ROLE_USER)).build();
            UserRequest updatedUserRequest = new UserRequest("New Name", "newPassword", Set.of(Role.ROLE_ADMIN.name()));
            User updatedUser = User.builder().id(1L).name("New Name").email(email).password("encodedNewPassword").roles(Set.of(Role.ROLE_ADMIN)).build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(updatedUserRequest.getPassword())).thenReturn("encodedNewPassword");
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            // Act
            UserResponse userResponse = userService.updateUserByEmail(email, updatedUserRequest);

            // Assert
            assertNotNull(userResponse);
            assertEquals("New Name", userResponse.getName());
            assertEquals(email, userResponse.getEmail());
            assertEquals(Set.of("ROLE_ADMIN"), userResponse.getRoles());

            verify(userRepository, times(1)).findByEmail(email);
            verify(passwordEncoder, times(1)).encode(updatedUserRequest.getPassword());
            verify(userRepository, times(1)).save(any(User.class));
            verify(userEventProducer, times(1)).sendEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // Arrange
            String email = "nonexistent@example.com";
            UserRequest updatedUserRequest = new UserRequest("New Name", "newPassword", Set.of(Role.ROLE_ADMIN.name()));
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.updateUserByEmail(email, updatedUserRequest));

            verify(userRepository, times(1)).findByEmail(email);
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
            verify(userEventProducer, never()).sendEvent(any(UserEvent.class));
        }
    }

    @Nested
    @DisplayName("deleteUserByEmail Tests")
    class DeleteUserByEmailTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Arrange
            String email = "test@example.com";
            User user = User.builder().id(1L).name("Test User").email(email).roles(Set.of(Role.ROLE_USER)).build();
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            doNothing().when(userRepository).deleteById(user.getId());

            // Act
            userService.deleteUserByEmail(email);

            // Assert
            verify(userRepository, times(1)).findByEmail(email);
            verify(userRepository, times(1)).deleteById(user.getId());
            verify(userEventProducer, times(1)).sendEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // Arrange
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.deleteUserByEmail(email));

            verify(userRepository, times(1)).findByEmail(email);
            verify(userRepository, never()).deleteById(anyLong());
            verify(userEventProducer, never()).sendEvent(any(UserEvent.class));
        }
    }
}
