package com.example.consultation_component.example.hateos;

import com.example.consultation_component.example.controller.ConsultationController;
import com.example.consultation_component.example.dto.ConsultationDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ConsultationHateoas  implements RepresentationModelAssembler<ConsultationDto, EntityModel<ConsultationDto>> {
    @Override
    public EntityModel<ConsultationDto> toModel(ConsultationDto appointmentDTO){
        EntityModel<ConsultationDto> patientModel = null;
        try {
            patientModel = EntityModel.of(appointmentDTO,
                    linkTo(methodOn(ConsultationController.class).getConsultationById(null,appointmentDTO.getId())).withSelfRel(),
                    linkTo(methodOn(ConsultationController.class).getAllConsultations()).withRel("parent"),
                    Link.of("http://localhost:8081/api/pos/appointments/"+appointmentDTO.getId_patient() + "/" +appointmentDTO.getId_physician() + "/" + URLEncoder.encode(appointmentDTO.getDate(), "UTF-8")).withRel("appointment")
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return patientModel;
    }
}
