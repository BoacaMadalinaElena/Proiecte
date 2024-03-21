package com.example.consultation_component.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ConsultationDto {
    @Id
    private String id;
    private Integer id_patient;
    private Integer id_physician;
    private String date;
    private DiagnosticType diagnostic;
    private List<InvestigationDto> investigations;
}
