package org.example.filter;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.example.filter.exceptions.UnauthorizedException;
import org.example.filter.model.ErrorDto;
import org.example.filter.model.UserAuthorizationDto;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.example.filter.model.Pair;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CustomGlobalFilter {
    private final AuthorizationService authorizationService;

    @Bean
    public GlobalFilter customFilter() {
        return (exchange, chain) -> {
            String requestUri = exchange.getRequest().getURI().toString();
            HttpMethod requestMethod = exchange.getRequest().getMethod();
            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            List<Pair<String, HttpMethod>> publicUrl = new ArrayList<>(List.of(
                    new Pair<>("/api/cluster/user/createAccount", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/validateAccount", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/login", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/change-password-send-code", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/change-password-check-code", HttpMethod.POST),
                    new Pair<>("/api/cluster/code/intern/", HttpMethod.GET),
                    new Pair<>("/api/cluster/ipAddress", HttpMethod.POST),
                    new Pair<>("/api/cluster/ipAddress", HttpMethod.DELETE),
                    new Pair<>("/api/cluster/user/validateJWS", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/validateJWSLogs", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/logoutJWS", HttpMethod.POST),
                    new Pair<>("/api/cluster/user/logoutJWSExtern", HttpMethod.POST),
                    new Pair<>("cluster/user/securityAttack", HttpMethod.POST),
                    new Pair<>("/api/cluster/ipAddress/ipAndPort/", HttpMethod.GET),
                    new Pair<>("/api/cluster/user/getUserById", HttpMethod.GET),
                    new Pair<>("/api/cluster/user/restart", HttpMethod.POST)

            ));


            System.out.println("Executing custom filter for route: " + requestUri);

            for (Pair<String, HttpMethod> pair : publicUrl) {
                if ((requestUri.contains(pair.getKey()) && pair.getValue() == requestMethod)) {
                    System.out.println(requestUri + " is public");
                    return chain.filter(exchange);
                }
            }


            try {
                UserAuthorizationDto authorizedInfo = authorizationService.authorized(authorizationHeader);

                String userId = authorizedInfo.getId();
                String role = authorizedInfo.getRoleType();

                ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Role", role)
                        .header("X-Email", authorizedInfo.getEmail())
                        .build();
                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                return chain.filter(mutatedExchange);
            } catch (UnauthorizedException unauthorizedException) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add("Content-Type", "application/json");
                Gson gson = new Gson();
                try {
                    ErrorDto errorDto = new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng());
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(gson.toJson(errorDto).getBytes())));
                } catch (Exception ex) {
                    String responseBody = gson.toJson(new ErrorDto("Acces neautorizat!", "Unauthorized!"));
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody.getBytes())));
                }
            }
        };
    }
}