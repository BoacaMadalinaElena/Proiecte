package com.example.httpnode.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TokenDto {
    private String token;
    private String ip;
}
