package com.knighteye097.journal_service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JournalServiceApplication {

	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaServers;

	public static void main(String[] args) {
		SpringApplication.run(JournalServiceApplication.class, args);
	}

	@PostConstruct
	public void logKafkaConfig() {
		System.out.println("📦 Kafka is set to: " + kafkaServers);
	}
}
