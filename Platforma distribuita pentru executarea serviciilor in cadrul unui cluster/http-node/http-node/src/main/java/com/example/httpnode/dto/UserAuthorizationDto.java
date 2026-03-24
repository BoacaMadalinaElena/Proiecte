package com.example.httpnode.dto;

import com.example.httpnode.dto.enums.RoleType;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserAuthorizationDto {
    private String id;
    private String roleType;
    private String email;
}
