package com.example.appointmentcomponent.hateoas;


import com.example.appointmentcomponent.controller.AppointmentsController;
import com.example.appointmentcomponent.dto.AppointmentDTO;
import com.example.appointmentcomponent.exceptions.NotAcceptableException;
import com.example.appointmentcomponent.service.ParameterValidationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AppointmentHateoas implements RepresentationModelAssembler<AppointmentDTO, EntityModel<AppointmentDTO>> {
    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    ParameterValidationsService parameterValidationsService;

    @Override
    public EntityModel<AppointmentDTO> toModel(AppointmentDTO appointmentDTO) {
        String formattedData = null;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = originalFormat.parse(appointmentDTO.getDate());

            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            formattedData = newFormat.format(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        EntityModel<AppointmentDTO> appointmentModel;
        try {
            String url = "http://localhost:8082/api/pos/consultation/" + appointmentDTO.getId_patient() + "/" + appointmentDTO.getId_physician() + "/" + formattedData;

            //System.out.println(url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            HttpStatusCode responseStatus = response.getStatusCode();
            String responseBody = response.getBody();

            //System.out.println(responseBody);
            if (responseStatus == HttpStatus.OK && !Objects.requireNonNull(responseBody).contains("\"result\":[]")) {
                appointmentModel = EntityModel.of(appointmentDTO,
                        linkTo(methodOn(AppointmentsController.class).getAppointmentsById(null,appointmentDTO.getId_patient(), appointmentDTO.getId_physician(), appointmentDTO.getDate())).withSelfRel(),
                        linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("parent"),
                        // id user e folosit ca si cheie straina
                        Link.of("http://localhost:8084/api/pos/patients/id_patient/" + appointmentDTO.getId_patient()).withRel("Patient"),
                        Link.of("http://localhost:8085/api/pos/physicians/id_user/" + appointmentDTO.getId_physician()).withRel("Physician"),
                        Link.of(url).withRel("consultation")
                );
            } else {
                appointmentModel = EntityModel.of(appointmentDTO,
                        linkTo(methodOn(AppointmentsController.class).getAppointmentsById(null,appointmentDTO.getId_patient(), appointmentDTO.getId_physician(), appointmentDTO.getDate())).withSelfRel(),
                        linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("parent"),
                        Link.of("http://localhost:8084/api/pos/patients/id_patient/" + appointmentDTO.getId_patient()).withRel("Patient"),
                        Link.of("http://localhost:8085/api/pos/physicians/id_user/" + appointmentDTO.getId_physician()).withRel("Physician")
                );
            }
        } catch (Exception ex) {
            appointmentModel = EntityModel.of(appointmentDTO,
                    linkTo(methodOn(AppointmentsController.class).getAppointmentsById(null,appointmentDTO.getId_patient(), appointmentDTO.getId_physician(), appointmentDTO.getDate())).withSelfRel(),
                    linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("parent"),
                    Link.of("http://localhost:8084/api/pos/patients/id_patient/" + appointmentDTO.getId_patient()).withRel("Patient"),
                    Link.of("http://localhost:8085/api/pos/physicians/id_user/" + appointmentDTO.getId_physician()).withRel("Physician")
            );
            System.out.println(ex.getMessage());
        }
        return appointmentModel;
    }
}
