package com.example.idm_component.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthorizedInfo {
    private String id;
    private String role;
    private String token;
}
