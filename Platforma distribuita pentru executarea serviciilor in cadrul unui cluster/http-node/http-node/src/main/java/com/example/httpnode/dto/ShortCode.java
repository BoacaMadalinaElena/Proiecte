package com.example.httpnode.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortCode {
    private String codeId;
    private String userId;
    private String title;
    private String description;
    private String username;
}
