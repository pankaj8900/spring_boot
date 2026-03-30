package com.example.aml.service;

import com.example.aml.domain.AmlAlert;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AmlAlertPublisher {
    private final KafkaTemplate<String, AmlAlert> kafkaTemplate;

    public AmlAlertPublisher(KafkaTemplate<String, AmlAlert> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AmlAlert alert) {
        kafkaTemplate.send("aml-alerts", alert.transactionId(), alert);
    }
}
