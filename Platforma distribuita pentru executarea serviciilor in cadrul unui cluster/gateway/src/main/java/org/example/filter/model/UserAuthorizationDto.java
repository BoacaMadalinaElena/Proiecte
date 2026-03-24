package org.example.filter.model;

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
