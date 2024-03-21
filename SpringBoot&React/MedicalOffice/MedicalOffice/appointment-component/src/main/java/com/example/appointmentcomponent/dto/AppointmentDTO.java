package com.example.appointmentcomponent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "appointment")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AppointmentPK.class)
public class AppointmentDTO {
    @Id
    private Long id_patient;
    @Id
    private Long id_physician;
    @Id
    private String date;

    private String status;
}
