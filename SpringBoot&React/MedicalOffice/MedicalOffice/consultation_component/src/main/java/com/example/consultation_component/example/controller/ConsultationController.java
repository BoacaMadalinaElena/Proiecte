package com.example.consultation_component.example.controller;

import com.example.consultation_component.example.dto.ConsultationDto;
import com.example.consultation_component.example.dto.ErrorDto;
import com.example.consultation_component.example.dto.InvestigationDto;
import com.example.consultation_component.example.dto.UserAuthorizationDto;
import com.example.consultation_component.example.exceptions.NotAcceptableException;
import com.example.consultation_component.example.exceptions.NotFoundException;
import com.example.consultation_component.example.hateos.ConsultationHateoas;
import com.example.consultation_component.example.service.AuthorizationService;
import com.example.consultation_component.example.service.ConsultationService;
import com.example.consultation_component.example.service.ParameterValidationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.catalina.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/pos/consultation")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private ParameterValidationsService parameterValidationsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationController.class);
    @Autowired
    private AuthorizationService authorizationService;


    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultations found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConsultationDto.class)))
                    }
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    public ResponseEntity<?> getAllConsultations() {

        LOGGER.info("Sau solicitat toate consultatiile");
        // metoda nu este expusa
        if (true)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<ConsultationDto> consultations = consultationService.findAll();
        if (!consultations.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (ConsultationDto p : consultations
            ) {
                list.add(new ConsultationHateoas().toModel(p));
            }
            LOGGER.info("Sau returnat toate inregistrarile");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(ConsultationController.class).getAllConsultations()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation found!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Consultation not found!"
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    @Operation(
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token for authentication",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> getConsultationById(@RequestHeader("Authorization") String authorizationHeader,@PathVariable String id) {
        LOGGER.info("Sa solicitat consultatia cu id-ul: " + id);
        Optional<ConsultationDto> consultation = consultationService.selectConsultationById(id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (consultation.isPresent()) {
            if(consultation.get().getId_physician() != userAuthorizationDto.getId() && consultation.get().getId_patient() != userAuthorizationDto.getId()){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            LOGGER.info("Sa returnat o consultatie");
            return new ResponseEntity<>(new ConsultationHateoas().toModel(consultation.get()), HttpStatus.OK);
        } else {
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(ConsultationController.class).getAllConsultations()).withRel("parent");

            arrayLinks.add(parentLink);
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Nu exista nici o consultatie de returnat");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Consultation deleted!"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultation not found!"
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    @Operation(
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token for authentication",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> deleteConsultation(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        LOGGER.info("Sa solicitat cererea consultatiei cu id-ul: " + id);
        Optional<ConsultationDto> consultationDto = this.consultationService.selectConsultationById(id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getRole() != 1 ) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                if (consultationDto.isPresent()) {
                    if (consultationDto.get().getId_physician() != userAuthorizationDto.getId()) {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        }
        try {
            consultationService.deleteConsultationById(id);
            LOGGER.info("Consultatia a fost stersa cu succes!");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException notFoundException) {
            LOGGER.info("Consultatia nu a putut fi stearsa deaorece nu exista");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Consultation modified!"
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Consultation created!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationDto.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Campuri invalide!"
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    @Operation(
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token for authentication",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @RequestBody ConsultationDto consultationDto) {
        Optional<ConsultationDto> consultationDTOUpdate = consultationService.selectConsultationById(id);
        LOGGER.info("Sa solicitat actualizarea consultatiei cu id-ul: " + id + " cu datele: " + consultationDTOUpdate);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (userAuthorizationDto.getRole() != 1) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (consultationDTOUpdate.isPresent() || !consultationService.selectConsultationByParameters(consultationDto.getId_patient(),consultationDto.getId_physician(),consultationDto.getDate()).isEmpty()) {
            // update doar de cel ce are consultatia
            if (userAuthorizationDto.getId() != consultationDto.getId_physician()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            consultationDto.setId(id);
            try {
                consultationService.updateConsultationById(authorizationHeader,id, consultationDto);
                LOGGER.info("Consultatia exista si a fost actualizata");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotAcceptableException notAcceptableException) {
                LOGGER.info(notAcceptableException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } else {
            try {
                consultationDto.setId(null);
                if (userAuthorizationDto.getId() != consultationDto.getId_physician()) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                ConsultationDto consultationDto1 = consultationService.createConsultation(authorizationHeader,consultationDto);
                LOGGER.info("Consultatia nu exista se solicita crearea uneiai noi");
                return new ResponseEntity<>(new ConsultationHateoas().toModel(consultationDto1), HttpStatus.CREATED);
            } catch (NotAcceptableException notAcceptableException) {
                LOGGER.info(notAcceptableException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Consultation created!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationDto.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Campuri invalide!"
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    @Operation(
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token for authentication",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> post(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ConsultationDto consultationDto) {
        LOGGER.info("Sa solicitat crearea unei resurse cu datele: " + consultationDto);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (userAuthorizationDto.getRole() != 1) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (userAuthorizationDto.getId() != consultationDto.getId_physician()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        consultationDto.setId_physician(userAuthorizationDto.getId());
        consultationDto.setId(null);
        try {
            consultationService.createConsultation(authorizationHeader,consultationDto);
            LOGGER.info("Resursa a fost creata");
            return new ResponseEntity<>(new ConsultationHateoas().toModel(consultationDto), HttpStatus.CREATED);
        } catch (NotAcceptableException notAcceptableException) {
            LOGGER.info(notAcceptableException.getMessage());
            return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PatchMapping("/{id}/investigations")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Investigations modified!"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Campuri invalide!"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nu exista o consultatie cu acest id!"
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    @Operation(
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token for authentication",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> updateInvestigations(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @RequestBody List<InvestigationDto> updatedInvestigations) {
        LOGGER.info("Sa solicitat actualizarea lisei de informatii pentru: " + id + " cu: " + updatedInvestigations);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        Optional<ConsultationDto> consultationDTOUpdate = consultationService.selectConsultationById(id);

        if (consultationDTOUpdate.isPresent()) {
            try {
                if (consultationDTOUpdate.get().getId_physician() != userAuthorizationDto.getId()) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                consultationService.updateInvestigations(id, updatedInvestigations);
                LOGGER.info("Actualizarea sa facut cu succes");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotAcceptableException notAcceptableException) {
                LOGGER.error(notAcceptableException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            LOGGER.info("Consultatia nu exista!");
            return new ResponseEntity<>(new ErrorDto("Consultation not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id_patient}/{id_physician}/{data}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation found!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Consultation not found!"
            ),
            @ApiResponse(
                    responseCode = "406", description = "Parametru invalid!"
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    @Operation(
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token for authentication",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> getAppointmentsById(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Integer id_patient, @PathVariable Integer id_physician, @PathVariable String data) {
        LOGGER.info("Sa solicitat consultatia cu datele: " + id_patient + " " + id_physician + " " + data);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            LocalDateTime dateTime = parameterValidationsService.isValidDate(data);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error(illegalArgumentException.getMessage());
            return new ResponseEntity<>(new ErrorDto("Data trebuie sa fie in formatul yyyy-MM-dd HH:mm!"), HttpStatus.NOT_ACCEPTABLE);
        }
        List<ConsultationDto> consultation = consultationService.selectConsultationByParameters(id_patient, id_physician, data);

        if (consultation != null) {
            return new ResponseEntity<>(consultation, HttpStatus.OK);
        } else {
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(ConsultationController.class).getAllConsultations()).withRel("parent");

            arrayLinks.add(parentLink);
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Nu exista data ceruta");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultations found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConsultationDto.class)))
                    }
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    public ResponseEntity<?> getAllConsultations(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Integer id) {

        LOGGER.info("Sau solicitat toate consultatiile pacientului: " + id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(id != userAuthorizationDto.getId()){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<ConsultationDto> consultations = consultationService.findAll();

        if (!consultations.isEmpty()) {
            List<ConsultationDto> filtered =  consultations.stream()
                    .filter(consultation -> consultation.getId_patient().equals(id))
                    .collect(Collectors.toList());
            List<EntityModel> list = new ArrayList<>();
            for (ConsultationDto p : filtered
            ) {
                list.add(new ConsultationHateoas().toModel(p));
            }
            LOGGER.info("Sau returnat toate inregistrarile");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(ConsultationController.class).getAllConsultations()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }
}
