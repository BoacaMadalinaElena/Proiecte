package com.example.httpnode.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorDto {
    private String messageRo;
    private String messageEng;
}
