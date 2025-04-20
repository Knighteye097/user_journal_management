package com.knighteye097.journal_service.repository;

import com.knighteye097.journal_service.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByEmail(String email);
    List<JournalEntry> findByType(String type);
    List<JournalEntry> findByTypeAndEmail(String type, String email);
}