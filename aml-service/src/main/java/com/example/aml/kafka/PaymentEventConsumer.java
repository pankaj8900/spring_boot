package com.example.aml.kafka;

import com.example.aml.domain.PaymentEvent;
import com.example.aml.service.AmlAlertPublisher;
import com.example.aml.service.AmlAlertStore;
import com.example.aml.service.AmlRuleEngine;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    private final AmlRuleEngine amlRuleEngine;
    private final AmlAlertStore alertStore;
    private final AmlAlertPublisher alertPublisher;

    public PaymentEventConsumer(AmlRuleEngine amlRuleEngine,
                                AmlAlertStore alertStore,
                                AmlAlertPublisher alertPublisher) {
        this.amlRuleEngine = amlRuleEngine;
        this.alertStore = alertStore;
        this.alertPublisher = alertPublisher;
    }

    @KafkaListener(topics = "payments", groupId = "aml-service")
    public void consume(PaymentEvent event) {
        amlRuleEngine.evaluate(event).ifPresent(alert -> {
            alertStore.add(alert);
            alertPublisher.publish(alert);
        });
    }
}
