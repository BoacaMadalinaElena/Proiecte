package com.example.httpnode.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUser {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String description;
}
