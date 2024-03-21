package com.example.consultation_component.example.repository;

import com.example.consultation_component.example.dto.ConsultationDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsultationRepository extends MongoRepository<ConsultationDto,String> {

}
