package com.example.consultation_component.example.dto;

import lombok.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class InvestigationDto {
    @Id
    private ObjectId id;
    private String name;
    private Long days;
    private String result;
}
