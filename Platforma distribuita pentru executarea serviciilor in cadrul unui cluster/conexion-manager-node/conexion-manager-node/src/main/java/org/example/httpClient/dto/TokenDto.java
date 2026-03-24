package org.example.httpClient.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String token;
    private String ip;
}
