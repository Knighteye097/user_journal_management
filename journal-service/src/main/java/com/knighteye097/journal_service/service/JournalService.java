package com.knighteye097.journal_service.service;

import com.knighteye097.journal_service.entity.EventType;
import com.knighteye097.journal_service.entity.JournalEntry;
import com.knighteye097.journal_service.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;

    public ResponseEntity<List<JournalEntry>> getAllEvents() {
        return ResponseEntity.ok(journalRepository.findAll());
    }

    public ResponseEntity<List<JournalEntry>> getMyEvents(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(journalRepository.findByEmail(email));
    }

    public ResponseEntity<List<JournalEntry>> getByEmail(String email, Authentication auth) {
        boolean isAdmin = hasRole(auth);
        String currentUserEmail = auth.getName();

        if (isAdmin) {
            if (null == email || email.equalsIgnoreCase("all")) {
                return ResponseEntity.ok(journalRepository.findAll());
            } else {
                return ResponseEntity.ok(journalRepository.findByEmail(email));
            }
        } else {
            if (null == email || email.equalsIgnoreCase(currentUserEmail)) {
                return ResponseEntity.ok(journalRepository.findByEmail(currentUserEmail));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    public ResponseEntity<List<JournalEntry>> getByType(EventType type, Authentication auth) {
        boolean isAdmin = hasRole(auth);
        String email = auth.getName();

        List<JournalEntry> entries;

        if(type == null) {
            entries = isAdmin
                    ? journalRepository.findAll()
                    : journalRepository.findByEmail(email);
        } else {
            entries = isAdmin
                    ? journalRepository.findByType(type)
                    : journalRepository.findByTypeAndEmail(type, email);
        }

        return ResponseEntity.ok(entries);
    }

    private boolean hasRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMIN"));
    }
}
