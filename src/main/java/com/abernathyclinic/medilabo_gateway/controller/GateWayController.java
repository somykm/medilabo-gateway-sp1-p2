package com.abernathyclinic.medilabo_gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
@Slf4j
@RestController
@RequestMapping("/api")
public class GateWayController {

    private final RestTemplate restTemplate;

    @Autowired
    public GateWayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/status")
    public String status() {
        return "Gateway is running and routing requests.";
    }

    @GetMapping("/patient/**")
    public ResponseEntity<?> forwardToPatient(ServerHttpRequest request) {
        String path = request.getPath().pathWithinApplication().value();
        String targetUrl = "http://localhost:8081" + path;
        log.info("Forwarding to Patient Service: {}", targetUrl);
        return restTemplate.getForEntity(targetUrl, Object.class);
    }
    @PutMapping("/patient/**")
    public ResponseEntity<?> forwardPutToPatient(ServerHttpRequest request, @RequestBody Object body) {
        String path = request.getPath().pathWithinApplication().value();
        String targetUrl = "http://localhost:8081" + path;

        log.info("Forwarding PUT to Patient Service: {}", targetUrl);

        HttpEntity<Object> entity = new HttpEntity<>(body);
        restTemplate.put(targetUrl, entity);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/patient/**")
    public ResponseEntity<?> forwardPostToPatient(ServerHttpRequest request, @RequestBody Object body) {
        String path = request.getPath().pathWithinApplication().value();
        String targetUrl = "http://localhost:8081" + path;

        log.info("Forwarding POST to Patient Service: {}", targetUrl);

        HttpEntity<Object> entity = new HttpEntity<>(body);
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(targetUrl, entity, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("POST forwarding failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("POST forwarding failed.");
        }
    }

    @GetMapping("/history/**")
    public ResponseEntity<?> forwardToHistory(ServerHttpRequest request) {
        String path = request.getPath().pathWithinApplication().value();
        String targetUrl = "http://localhost:8083" + path;
        log.info("Forwarding to History Service: {}", targetUrl);
        return restTemplate.getForEntity(targetUrl, Object.class);
    }
    @PostMapping("/history/**")
    public ResponseEntity<?> forwardPostToHistory(ServerHttpRequest request, @RequestBody Object body) {
        String path = request.getPath().pathWithinApplication().value();
        String targetUrl = "http://localhost:8083" + path;
        log.info("Forwarding POST to History Service: {}", targetUrl);
        HttpEntity<Object> entity = new HttpEntity<>(body);
        return restTemplate.postForEntity(targetUrl, entity, Object.class);
    }
}