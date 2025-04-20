package com.knighteye097.journal_service.service;

import com.knighteye097.journal_service.entity.EventType;
import com.knighteye097.journal_service.entity.JournalEntry;
import com.knighteye097.journal_service.repository.JournalRepository;
import com.knighteye097.journal_service.service.impl.JournalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DisplayName("JournalServiceImpl Test Suite")
class JournalServiceImplTest {

    @Mock
    private JournalRepository journalRepository;

    @InjectMocks
    private JournalServiceImpl journalService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Tests for GetAllEvents")
    class GetAllEventsTests {

        @Test
        @DisplayName("Positive: Should return all events")
        void shouldReturnAllEvents() {
            // Arrange
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findAll()).thenReturn(dummyEntries);

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getAllEvents();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }
    }

    @Nested
    @DisplayName("Tests for GetMyEvents")
    class GetMyEventsTests {

        @Test
        @DisplayName("Positive: Should return events for the authenticated user")
        void shouldReturnEventsForAuthenticatedUser() {
            // Arrange
            String userEmail = "user@example.com";
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findByEmail(userEmail)).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getMyEvents(auth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }
    }

    @Nested
    @DisplayName("Tests for GetByEmail")
    class GetByEmailTests {

        @Test
        @DisplayName("Positive: Admin should get all events when email is null")
        void adminShouldGetAllEventsWhenEmailIsNull() {
            // Arrange
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findAll()).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
                    "admin@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByEmail(null, adminAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Positive: Admin should get all events when email is 'all'")
        void adminShouldGetAllEventsWhenEmailIsAll() {
            // Arrange
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findAll()).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
                    "admin@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByEmail("all", adminAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Positive: Admin should retrieve events for a specific user")
        void adminShouldRetrieveEventsForSpecificUser() {
            // Arrange
            String targetEmail = "user@example.com";
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findByEmail(targetEmail)).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
                    "admin@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByEmail(targetEmail, adminAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Positive: Non-admin should retrieve own events")
        void nonAdminShouldRetrieveOwnEvents() {
            // Arrange
            String userEmail = "user@example.com";
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findByEmail(userEmail)).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
                    userEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByEmail(userEmail, userAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Negative: Non-admin should be forbidden when requesting different user's events")
        void nonAdminShouldBeForbiddenWhenRequestingDifferentUsersEvents() {
            // Arrange
            String userEmail = "user@example.com";
            String anotherUser = "another@example.com";
            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
                    userEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByEmail(anotherUser, userAuth);

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }

        @Test
        @DisplayName("Negative: Non-admin should be forbidden when email filter is 'all'")
        void nonAdminShouldBeForbiddenWhenEmailIsAll() {
            // Arrange
            String userEmail = "user@example.com";
            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
                    userEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByEmail("all", userAuth);

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Tests for GetByType")
    class GetByTypeTests {

        @Test
        @DisplayName("Positive: Admin should retrieve events by type")
        void adminShouldRetrieveEventsByType() {
            // Arrange
            EventType eventType = EventType.ALL_USER_FETCHED; // Adjust as necessary.
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findByType(eventType)).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
                    "admin@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByType(eventType, adminAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Positive: Non-admin should retrieve events by type for own email")
        void nonAdminShouldRetrieveEventsByTypeForOwnEmail() {
            // Arrange
            String userEmail = "user@example.com";
            EventType eventType = EventType.USER_UPDATED; // Adjust as necessary.
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findByTypeAndEmail(eventType, userEmail)).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
                    userEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByType(eventType, userAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Positive: Admin should retrieve all events when type is null")
        void adminShouldRetrieveAllEventsWhenTypeIsNull() {
            // Arrange
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findAll()).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
                    "admin@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByType(null, adminAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }

        @Test
        @DisplayName("Positive: Non-admin should retrieve own events when type is null")
        void nonAdminShouldRetrieveOwnEventsWhenTypeIsNull() {
            // Arrange
            String userEmail = "user@example.com";
            List<JournalEntry> dummyEntries = List.of(new JournalEntry());
            when(journalRepository.findByEmail(userEmail)).thenReturn(dummyEntries);
            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
                    userEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Act
            ResponseEntity<List<JournalEntry>> response = journalService.getByType(null, userAuth);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(dummyEntries, response.getBody());
        }
    }
}