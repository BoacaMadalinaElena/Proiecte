package com.example.httpnode.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IpAndTimeDto {
    private String ip;
    private LocalDateTime time;
}
