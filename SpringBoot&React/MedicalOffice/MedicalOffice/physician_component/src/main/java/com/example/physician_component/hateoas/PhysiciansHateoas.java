package com.example.physician_component.hateoas;

import com.example.physician_component.controller.PhysiciansController;
import com.example.physician_component.dto.Pair;
import com.example.physician_component.dto.PhysiciansDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PhysiciansHateoas implements RepresentationModelAssembler<Pair<String,PhysiciansDTO>, EntityModel<PhysiciansDTO>> {
    RestTemplate restTemplate = new RestTemplate();
    @Override
    public EntityModel<PhysiciansDTO> toModel(Pair<String,PhysiciansDTO> physician) {
        EntityModel<PhysiciansDTO> physiciansModel;
     try {
         String url = "http://localhost:8081/api/pos/appointments/physicians/" + physician.getValue().getId_user() + "/patients";
         HttpHeaders headers = new HttpHeaders();
         headers.set("Authorization", physician.getKey());
         HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
         ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
         HttpStatusCode responseStatus = response.getStatusCode();
         String responseBody = response.getBody();
         System.out.println(responseBody);

         if (responseStatus == HttpStatus.OK && !Objects.requireNonNull(responseBody).contains("\"result\":[]")) {
             physiciansModel = EntityModel.of(physician.getValue(),
                     linkTo(methodOn(PhysiciansController.class).getPhysicianById(null, physician.getValue().getId_physician())).withSelfRel(),
                     linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("parent"),
                     Link.of(url).withRel("appointments").withType("GET"),
                     Link.of("http://localhost:8082/api/pos/consultation/").withRel("consultation").withType("POST")
             );
         } else {
             physiciansModel = EntityModel.of(physician.getValue(),
                     linkTo(methodOn(PhysiciansController.class).getPhysicianById(null, physician.getValue().getId_physician())).withSelfRel(),
                     linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("parent")
             );
         }
     }catch (Exception ex){
         physiciansModel = EntityModel.of(physician.getValue(),
                 linkTo(methodOn(PhysiciansController.class).getPhysicianById(null, physician.getValue().getId_physician())).withSelfRel(),
                 linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("parent")
         );
     }
        return physiciansModel;
    }
}
