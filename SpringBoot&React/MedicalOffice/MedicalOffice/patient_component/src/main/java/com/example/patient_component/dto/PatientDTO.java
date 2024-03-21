package com.example.patient_component.dto;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Data
public class PatientDTO {
    @Id
    @Column(name = "cnp")
    private String cnp;

    @Column(name = "id_user")
    private Long user_id;

    @Column(name = "first_Name")
    private String firstName;

    @Column(name = "last_Name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "birthdate")
    private String birthDay;

    @Column(name = "is_active")
    private Boolean is_active;

    public PatientDTO(String cnp, Long user, String firstName, String lastName, String email, String telephone, String birthDay, Boolean isAlive) {
        this.cnp = cnp;
        this.user_id = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
        this.birthDay = birthDay;
        this.is_active = isAlive;
    }

    public PatientDTO() {
    }
}