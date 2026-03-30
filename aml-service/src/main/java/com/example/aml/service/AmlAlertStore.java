package com.example.aml.service;

import com.example.aml.domain.AmlAlert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AmlAlertStore {
    private final CopyOnWriteArrayList<AmlAlert> alerts = new CopyOnWriteArrayList<>();

    public void add(AmlAlert alert) {
        alerts.add(alert);
    }

    public List<AmlAlert> findAll() {
        return List.copyOf(alerts);
    }
}
