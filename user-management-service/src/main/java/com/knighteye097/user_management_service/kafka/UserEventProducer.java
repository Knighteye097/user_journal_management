package com.knighteye097.user_management_service.kafka;

import com.knighteye097.user_management_service.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "user-events";

    public void sendUserCreatedEvent(User user) {
        String message = String.format("User created: %s (%s)", user.getName(), user.getEmail());

        // 🚧 If Kafka is not running, comment the below line
        // kafkaTemplate.send(TOPIC, message);

        log.info("[MOCK KAFKA] Would publish to '{}': {}", TOPIC, message);
    }
}