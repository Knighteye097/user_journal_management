package com.knighteye097.user_management_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knighteye097.user_management_service.dto.UserEvent;
import com.knighteye097.user_management_service.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "user-events";

    @PostConstruct
    public void showKafkaProducerConfig() {
        Map<String, Object> configs = kafkaTemplate.getProducerFactory().getConfigurationProperties();
        System.out.println("KafkaTemplate is using: " + configs.get("bootstrap.servers"));
    }

    public void sendEvent(UserEvent event) {
        try {
            String json = new ObjectMapper().writeValueAsString(event);
            kafkaTemplate.send(TOPIC, json);

            log.info("Sent Kafka message to '{}': {}", TOPIC, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserEvent: ", e);
        }
    }
}