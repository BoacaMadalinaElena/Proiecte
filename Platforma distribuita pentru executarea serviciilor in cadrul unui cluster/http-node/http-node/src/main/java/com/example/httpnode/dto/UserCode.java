package com.example.httpnode.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCode {
    String email;
    long code;
    @JsonIgnore
    LocalDateTime time;


}
