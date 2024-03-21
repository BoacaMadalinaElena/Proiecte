package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("patient", r -> r
                        .path("/api/pos/patients/**")
                        .uri("http://localhost:8084"))
                .route("physician", r -> r
                        .path("/api/pos/physicians/**")
                        .uri("http://localhost:8085"))
                .route("appointments", r -> r
                        .path("/api/pos/appointments/**")
                        .uri("http://localhost:8081"))
                .route("consultations", r -> r
                        .path("/api/pos/consultation/**")
                        .uri("http://localhost:8082"))
                .route("idm", r -> r
                        .path("/api/pos/idm/**")
                        .uri("http://localhost:8090"))
                .build();
    }
}
