package com.example.httpnode.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class SecurityAttackInfo {
    private int number;
    private LocalDateTime blockDate;
}
