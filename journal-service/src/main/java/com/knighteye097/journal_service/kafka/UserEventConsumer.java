package com.knighteye097.journal_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knighteye097.journal_service.dto.UserEvent;
import com.knighteye097.journal_service.entity.JournalEntry;
import com.knighteye097.journal_service.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final JournalRepository journalRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "journal-group")
    public void consume(String message) {
        try {
            UserEvent event = objectMapper.readValue(message, UserEvent.class);

            JournalEntry entry = JournalEntry.builder()
                    .email(event.getEmail())
                    .name(event.getName())
                    .type(event.getType())
                    .message(event.getMessage())
                    .timestamp(event.getTimestamp())
                    .build();

            journalRepository.save(entry);
            log.info("Journal entry saved: {}", entry);

        } catch (Exception e) {
            log.error("Failed to process event: {}", message, e);
        }
    }
}