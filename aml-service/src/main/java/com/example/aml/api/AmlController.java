package com.example.aml.api;

import com.example.aml.domain.AmlAlert;
import com.example.aml.service.AmlAlertStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/aml")
public class AmlController {
    private final AmlAlertStore alertStore;

    public AmlController(AmlAlertStore alertStore) {
        this.alertStore = alertStore;
    }

    @GetMapping("/alerts")
    public List<AmlAlert> alerts() {
        return alertStore.findAll();
    }
}
