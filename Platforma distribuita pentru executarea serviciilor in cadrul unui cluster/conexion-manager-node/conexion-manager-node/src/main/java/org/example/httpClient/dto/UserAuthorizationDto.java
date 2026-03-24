package org.example.httpClient.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorizationDto {
    private String id;
    private String roleType;
}