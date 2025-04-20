package com.knighteye097.journal_service.controller;

import com.knighteye097.journal_service.entity.EventType;
import com.knighteye097.journal_service.entity.JournalEntry;
import com.knighteye097.journal_service.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class JournalController {

    private final JournalService journalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JournalEntry>> getAllEvents() {
        return journalService.getAllEvents();
    }

    @GetMapping("/email/self")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<JournalEntry>> getMyEvents(Authentication authentication) {
        return journalService.getMyEvents(authentication);
    }

    @GetMapping("/email")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<JournalEntry>> getByEmail(@RequestParam(required = false) String email, Authentication auth) {
        return journalService.getByEmail(email, auth);
    }

    @GetMapping("/type")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<JournalEntry>> getByType(@RequestParam(required = false) EventType type, Authentication auth) {
        return journalService.getByType(type, auth);
    }
}
