package com.knighteye097.journal_service.controller;

import com.knighteye097.journal_service.entity.UserEvent;
import com.knighteye097.journal_service.repository.UserEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class JournalController {

    private final UserEventRepository userEventRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEvent>> getAllEvents() {
        return ResponseEntity.ok(userEventRepository.findAll());
    }
}