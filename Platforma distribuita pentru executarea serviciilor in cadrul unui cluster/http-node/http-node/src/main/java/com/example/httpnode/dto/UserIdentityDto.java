package com.example.httpnode.dto;

import com.example.httpnode.dto.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentityDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String description;
    private String token;
    private String roleType;
}
