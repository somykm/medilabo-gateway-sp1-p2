package com.abernathyclinic.medilabo_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<String> fallback() {
        log.warn("Fallback triggered: Patient service is currently unavailable.");
        return ResponseEntity.ok("Fallback response: Patient service is currently unavailable.");
    }
}
