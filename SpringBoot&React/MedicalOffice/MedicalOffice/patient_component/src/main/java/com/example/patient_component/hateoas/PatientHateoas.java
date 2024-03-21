package com.example.patient_component.hateoas;


import com.example.patient_component.controller.PatientController;
import com.example.patient_component.dto.Pair;
import com.example.patient_component.dto.PatientDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.hateoas.Link;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatientHateoas implements RepresentationModelAssembler<Pair<String,PatientDTO>, EntityModel<PatientDTO>> {
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public EntityModel<PatientDTO> toModel(Pair<String,PatientDTO> patient) {
        EntityModel<PatientDTO> patientModel;
        try {
            // verific ca are programari
            String url = "http://localhost:8081/api/pos/appointments/patients/" + patient.getValue().getUser_id() + "/physicians";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", patient.getKey());
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            HttpStatusCode responseStatus = response.getStatusCode();
            String responseBody = response.getBody();

            // verific ca are consultatii
            String url2 = "http://localhost:8082/api/pos/consultation/patient/" +  patient.getValue().getUser_id();
            HttpEntity<Void> requestEntity2 = new HttpEntity<>(headers);
            ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.GET, requestEntity2, String.class);
            HttpStatusCode responseStatus2 = response2.getStatusCode();

            System.out.println(responseBody);
            if (responseStatus2 == HttpStatus.OK && !Objects.requireNonNull(responseBody).contains("\"result\":[]")) {
                patientModel = EntityModel.of(patient.getValue(),
                        linkTo(methodOn(PatientController.class).getPatientById(null, patient.getValue().getCnp())).withSelfRel().withType("GET"),
                        linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent").withType("GET"),
                        //isi poate vedea programarile
                        Link.of(url).withRel("appointments").withType("GET"),
                        Link.of(url2).withRel("consultations").withType("GET"),
                        // poate crea programare
                        Link.of("http://localhost:8081/api/pos/appointments/" + patient.getValue().getUser_id() + "/{id_physician}/{date}").withRel("appointments").withType("PUT")
                );
            }
            else if (responseStatus == HttpStatus.OK && !Objects.requireNonNull(responseBody).contains("\"result\":[]")) {
                patientModel = EntityModel.of(patient.getValue(),
                        linkTo(methodOn(PatientController.class).getPatientById(null, patient.getValue().getCnp())).withSelfRel().withType("GET"),
                        linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent").withType("GET"),
                        //isi poate vedea programarile
                        Link.of(url).withRel("appointments").withType("GET"),
                        // poate crea programare
                        Link.of("http://localhost:8081/api/pos/appointments/" + patient.getValue().getUser_id() + "/{id_physician}/{date}").withRel("appointments").withType("PUT")
                );
            } else {
                patientModel = EntityModel.of(patient.getValue(),
                        linkTo(methodOn(PatientController.class).getPatientById(null, patient.getValue().getCnp())).withSelfRel().withType("GET"),
                        linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent").withType("GET"),
                        Link.of("http://localhost:8081/api/pos/appointments/" + patient.getValue().getUser_id() + "/{id_physician}/{date}").withRel("appointments").withType("PUT")
                );
            }
            // daca apar exceptii sunt de la legatura cu tabela programari
        }catch (Exception ex){
            patientModel = EntityModel.of(patient.getValue(),
                    linkTo(methodOn(PatientController.class).getPatientById(null,patient.getValue().getCnp())).withSelfRel().withType("GET"),
                    linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent").withType("GET"),
                    Link.of("http://localhost:8081/api/pos/appointments/"  + patient.getValue().getUser_id() + "/{id_physician}/{date}").withRel("appointments").withType("PUT")
            );
        }
        return patientModel;
    }
}
