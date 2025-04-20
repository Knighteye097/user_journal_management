package com.knighteye097.journal_service.service;

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
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        String currentUserEmail = auth.getName();

        if (isAdmin) {
            if (email.equalsIgnoreCase("all")) {
                return ResponseEntity.ok(journalRepository.findAll());
            } else {
                return ResponseEntity.ok(journalRepository.findByEmail(email));
            }
        } else {
            if (email.equalsIgnoreCase(currentUserEmail)) {
                return ResponseEntity.ok(journalRepository.findByEmail(email));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    public ResponseEntity<List<JournalEntry>> getByType(String type, Authentication auth) {
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");

        if (isAdmin) {
            return ResponseEntity.ok(journalRepository.findByType(type));
        } else {
            return ResponseEntity.ok(
                    journalRepository.findByTypeAndEmail(type, auth.getName())
            );
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals(role));
    }
}
