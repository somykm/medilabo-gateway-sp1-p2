package com.abernathyclinic.medilabo_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MedilaboGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(MedilaboGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("patient-service", r -> r
                        .path("/api/patient/**")

                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "MedilaboGateway")
                                .circuitBreaker(config -> config
                                        .setName("patientCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")))
                        .uri("http://localhost:8081"))
                // Route to frontend service
                .route("frontend-service", r -> r
                        .path("/ui/**")
                        .uri("http://localhost:8082"))
                .route("test-route", r -> r
                        .path("/test/**")
                        .uri("http://localhost:8081"))
                .build();
    }
}