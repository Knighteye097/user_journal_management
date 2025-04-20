package com.knighteye097.journal_service.service;

import com.knighteye097.journal_service.entity.EventType;
import com.knighteye097.journal_service.entity.JournalEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing journal entries.
 * <p>
 * Provides methods to retrieve journal events based on different criteria like all events,
 * the events of a specific user, events filtered by email, or events filtered by event type.
 * </p>
 */
public interface JournalService {

    /**
     * Retrieves all journal entries.
     *
     * @return a ResponseEntity containing a list of all JournalEntry objects.
     */
    ResponseEntity<List<JournalEntry>> getAllEvents();

    /**
     * Retrieves the journal entries specific to the authenticated user.
     *
     * @param authentication the Authentication object containing the user's details.
     * @return a ResponseEntity containing a list of JournalEntry objects for the authenticated user.
     */
    ResponseEntity<List<JournalEntry>> getMyEvents(Authentication authentication);

    /**
     * Retrieves journal entries filtered by email.
     * <p>
     * If the authenticated user is an admin, it will return all entries or those for the specified email.
     * For non-admin users, it will only return entries corresponding to the user's own email.
     * </p>
     *
     * @param email the email filter; can be null or "all" for all entries.
     * @param auth  the Authentication object containing the user's details.
     * @return a ResponseEntity containing a list of matching JournalEntry objects,
     * or a FORBIDDEN status for non-admins requesting entries of another user.
     */
    ResponseEntity<List<JournalEntry>> getByEmail(String email, Authentication auth);

    /**
     * Retrieves journal entries filtered by event type.
     * <p>
     * If the type is null:
     * <ul>
     *     <li>Admins receive all entries.</li>
     *     <li>Non-admins receive only entries for their own email.</li>
     * </ul>
     * Otherwise, admins receive all entries for that type, while non-admins receive only their own.
     * </p>
     *
     * @param type the event type filter.
     * @param auth the Authentication object containing the user's details.
     * @return a ResponseEntity containing a list of JournalEntry objects matching the filter.
     */
    ResponseEntity<List<JournalEntry>> getByType(EventType type, Authentication auth);
}