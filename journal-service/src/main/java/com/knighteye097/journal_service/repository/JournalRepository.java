package com.knighteye097.journal_service.repository;

import com.knighteye097.journal_service.entity.EventType;
import com.knighteye097.journal_service.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByEmail(String email);
    List<JournalEntry> findByType(EventType type);
    List<JournalEntry> findByTypeAndEmail(EventType type, String email);
}