package com.example.idm_component.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;
    private Long id_role;
    private String username;
    private String password;
}
