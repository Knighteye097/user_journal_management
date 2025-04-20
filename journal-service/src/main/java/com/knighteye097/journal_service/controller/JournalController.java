package com.knighteye097.journal_service.controller;

import com.knighteye097.journal_service.entity.EventType;
import com.knighteye097.journal_service.entity.JournalEntry;
import com.knighteye097.journal_service.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Tag(name = "Journal Controller", description = "Endpoints for managing journal entries")
public class JournalController {

    private final JournalService journalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all events", description = "Returns all journal events. Accessible only by admin users.")
    public ResponseEntity<List<JournalEntry>> getAllEvents() {
        return journalService.getAllEvents();
    }

    @GetMapping("/email/self")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get own events", description = "Returns journal entries for the authenticated user.")
    public ResponseEntity<List<JournalEntry>> getMyEvents(
            @Parameter(description = "Authentication token of the user") Authentication authentication) {
        return journalService.getMyEvents(authentication);
    }

    @GetMapping("/email")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get events by email", description = "Returns journal events filtered by email. Admins can query any user while non-admins only access their own.")
    public ResponseEntity<List<JournalEntry>> getByEmail(
            @Parameter(description = "Email filter; null or 'all' returns all entries if admin") @RequestParam(required = false) String email,
            @Parameter(description = "Authentication token of the user") Authentication auth) {
        return journalService.getByEmail(email, auth);
    }

    @GetMapping("/type")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get events by type", description = "Returns journal events filtered by event type. If type is null, admins receive all while non-admins receive only their own.")
    public ResponseEntity<List<JournalEntry>> getByType(
            @Parameter(description = "Event type filter") @RequestParam(required = false) EventType type,
            @Parameter(description = "Authentication token of the user") Authentication auth) {
        return journalService.getByType(type, auth);
    }
}
