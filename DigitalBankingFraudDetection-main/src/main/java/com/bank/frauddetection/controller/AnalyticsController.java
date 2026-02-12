package com.bank.frauddetection.controller;

import com.bank.frauddetection.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/export")
    public String exportTrainingData() {
        return analyticsService.exportTrainingData();
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return analyticsService.getFraudSummary();
    }

    @GetMapping("/fraud-by-location")
    public Map<String, Integer> fraudByLocation() {
        return analyticsService.getFraudByLocation();
    }
}
