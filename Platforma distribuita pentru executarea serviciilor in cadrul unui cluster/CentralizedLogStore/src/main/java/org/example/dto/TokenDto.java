package org.example.dto;

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
