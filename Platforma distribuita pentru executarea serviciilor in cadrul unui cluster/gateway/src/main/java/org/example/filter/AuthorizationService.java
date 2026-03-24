package org.example.filter;

import com.google.gson.Gson;
import org.example.filter.model.ErrorDto;
import org.example.filter.model.UserAuthorizationDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.example.filter.exceptions.UnauthorizedException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthorizationService {
    private final RestTemplate restTemplate = new RestTemplate();

    public UserAuthorizationDto authorized(String token) throws UnauthorizedException {

        HttpEntity<Map<String, String>> requestEntity = getMapHttpEntity(token);

        try {
            String url = "http://localhost:9040/api/cluster/user/validateJWSLogs";
            ResponseEntity<UserAuthorizationDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    UserAuthorizationDto.class
            );
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            Gson gson = new Gson();
            ErrorDto errorDto = gson.fromJson(responseBody, ErrorDto.class);
            if (errorDto == null) {
                throw new UnauthorizedException("Sesiunea a expirat vă rugăm să vă reconectați!", "The session has expired, please reconnect!");
            }
            System.err.println("A aparut o exceptie la validarea token-ului, nu se permite accesul: " + e.getMessage());
            throw new UnauthorizedException(errorDto.getMessageRo(), errorDto.getMessageEng());
        }
    }

    private static HttpEntity<Map<String, String>> getMapHttpEntity(String token) throws UnauthorizedException {
        if (token == null) {
            throw new UnauthorizedException("Token-ul este null", "Token is null!");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", token);
        requestBody.put("ip", "string");
        return new HttpEntity<>(requestBody, headers);
    }
}
