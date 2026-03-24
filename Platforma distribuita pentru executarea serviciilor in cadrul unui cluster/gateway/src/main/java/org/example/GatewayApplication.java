package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("logs", r -> r
                        .path("/api/cluster/logs/**")
                        .uri("http://localhost:8070"))
                .route("cluster", r -> r
                        .path("/api/cluster/code/**")
                        .uri("http://localhost:9010"))
                .route("feedback", r -> r
                        .path("/api/cluster/feedback/**")
                        .uri("http://localhost:9020"))
                .route("ipAddress", r -> r
                        .path("/api/cluster/ipAddress/**")
                        .uri("http://localhost:9030"))
                .route("user", r -> r
                        .path("/api/cluster/user/**")
                        .uri("http://localhost:9040"))
                .build();
    }
}


// Docker
/*
    .route("logs", r -> r
                        .path("/api/cluster/logs/**")
                        .uri("https://centralized_log_store:8070"))
                .route("cluster", r -> r
                        .path("/api/cluster/code/**")
                        .uri("https://code_component:9010"))
                .route("feedback", r -> r
                        .path("/api/cluster/feedback/**")
                        .uri("https://feedback_component:9020"))
                .route("ipAddress", r -> r
                        .path("/api/cluster/ipAddress/**")
                        .uri("https://address-component-microservice:9030"))
                .route("user", r -> r
                        .path("/api/cluster/user/**")
                        .uri("https://idm_node:9040"))
                .build();
*/