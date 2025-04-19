package com.knighteye097.journal_service.kafka;

import com.knighteye097.journal_service.entity.UserEvent;
import com.knighteye097.journal_service.repository.UserEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserEventRepository repository;

    @KafkaListener(topics = "user-events", groupId = "journal-group")
    public void consume(String message) {
        log.info("Received user event: {}", message);

        UserEvent event = UserEvent.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        repository.save(event);
    }
}