package com.example.idm_component.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CredentialsDto {
    private String username;
    private String password;
}
