package org.example.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserAuthorizationDto {
    private String id;
    private String roleType;
}
