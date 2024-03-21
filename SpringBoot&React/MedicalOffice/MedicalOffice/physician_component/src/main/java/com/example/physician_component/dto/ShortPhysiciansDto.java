package com.example.physician_component.dto;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ShortPhysiciansDto {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String specialization;
}
