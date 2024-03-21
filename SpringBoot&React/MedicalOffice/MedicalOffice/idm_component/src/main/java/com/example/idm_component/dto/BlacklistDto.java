package com.example.idm_component.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BlacklistDto {
    private String id;
    private LocalDateTime timestamp;
    private String authorizationToken;

    public BlacklistDto(LocalDateTime timestamp, String authorizationToken) {
        this.timestamp = timestamp;
        this.authorizationToken = authorizationToken;
    }
}
