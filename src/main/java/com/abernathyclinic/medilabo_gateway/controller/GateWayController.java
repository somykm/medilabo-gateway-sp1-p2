package com.abernathyclinic.medilabo_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GateWayController {
    @GetMapping("/status")
    public String status() {
        return "Gateway is running and routing requests.";
    }
}