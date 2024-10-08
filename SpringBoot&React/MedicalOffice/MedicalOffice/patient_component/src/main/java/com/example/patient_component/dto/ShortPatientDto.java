package com.example.patient_component.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortPatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
}
