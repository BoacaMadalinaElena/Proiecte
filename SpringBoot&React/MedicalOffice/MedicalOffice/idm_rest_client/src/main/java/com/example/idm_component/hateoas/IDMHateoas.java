package com.example.idm_component.hateoas;


import com.example.idm_component.dto.AuthorizedInfo;
import com.example.idm_component.dto.Pair;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class IDMHateoas implements RepresentationModelAssembler<Pair<String, AuthorizedInfo>, EntityModel<AuthorizedInfo>> {
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public EntityModel<AuthorizedInfo> toModel(Pair<String,AuthorizedInfo> idm) {
        System.out.println(idm.getValue());
        EntityModel<AuthorizedInfo> authorizationModel;
            if(Objects.equals(idm.getValue().getRole(), "2")) {
                authorizationModel = EntityModel.of(idm.getValue(),
                        Link.of("http://localhost:8086/api/medical_office/patients/id_patient/" + idm.getValue().getId()).withRel("physician").withType("GET")
                );
            }else if(Objects.equals(idm.getValue().getRole(), "1")){
                authorizationModel = EntityModel.of(idm.getValue(),
                        Link.of("http://localhost:8086/api/medical_office/physicians/id_user/" + idm.getValue().getId()).withRel("patient").withType("GET")
                        );
            }else{
                authorizationModel =null;
            }
            return  authorizationModel;
    }
}
