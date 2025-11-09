package com.abernathyclinic.medilabo_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MedilaboGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(MedilaboGatewayApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Patient Service (Backend on port 8081)
                .route("patient-service", r -> r
                        .path("/api/patient/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8081"))
                //History Service (Backend on port 8083)
                .route("history-service", r -> r
                        .path("/api/history/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8083"))
                //Frontend UI (Thymeleaf app on port 8082)
                .route("frontend-ui", r -> r
                        .path("/ui/**")
                        .uri("http://localhost:8082"))
                .build();

    }
}