package com.example.appointmentcomponent.controller;


import com.example.appointmentcomponent.dto.AppointmentDTO;
import com.example.appointmentcomponent.dto.ErrorDto;
import com.example.appointmentcomponent.dto.UserAuthorizationDto;
import com.example.appointmentcomponent.dto.type.TypeSelectDate;
import com.example.appointmentcomponent.exceptions.NotAcceptableException;
import com.example.appointmentcomponent.exceptions.NotFoundException;
import com.example.appointmentcomponent.exceptions.PermissionDeniedException;
import com.example.appointmentcomponent.hateoas.AppointmentHateoas;
import com.example.appointmentcomponent.service.AppointmentService;
import com.example.appointmentcomponent.service.AuthorizationService;
import com.example.appointmentcomponent.service.ParameterValidationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.Link;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/pos/appointments")
public class AppointmentsController {
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ParameterValidationsService parameterValidationsService;
    @Autowired
    private AuthorizationService authorizationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentsController.class);

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
            ),      @ApiResponse(
            responseCode = "401", description = "Token invalid!"
    ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),
    })
    public ResponseEntity<?> getAllAppointments() {
        LOGGER.info("O cerere de getAll");
        // metoda nu este expusa in exterior(adminul nu poate vedea toate datele, iar doctorul/pacienti isi pot vedea doar datele lor)
        if(true)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<AppointmentDTO> appointments = appointmentService.findAll();
        if (!appointments.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : appointments
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("Sa returnat un array nevid");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat un array cu elemente");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }

    @GetMapping("/{id_patient}/{id_physician}/{data}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment found!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Appointment not found!"
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
    public ResponseEntity<?> getAppointmentsById(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id_patient, @PathVariable Long id_physician, @PathVariable String data) {
        LOGGER.info("Sa facut cerere pentru programarea cu datele: " + id_patient + " " + id_physician + " " + data);
        UserAuthorizationDto userAuthorizationDto;
        try {
             userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 2 || userAuthorizationDto.getRole() == 1){
                // trebuie sa fie doctor sau pacient
                if(id_patient != userAuthorizationDto.getId() && id_physician != userAuthorizationDto.getId()){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        LocalDateTime dateTime;
        try {
            dateTime = parameterValidationsService.isValidDate(data);
            System.out.println(dateTime);
        } catch (Exception exception) {
            LOGGER.error("Data trebuie sa fie in formatul yyyy-MM-dd HH:mm!");
            return new ResponseEntity<>(new ErrorDto("Data trebuie sa fie in formatul yyyy-MM-dd HH:mm!"), HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<AppointmentDTO> appointment = appointmentService.selectAppointmentsByParameters(id_patient, id_physician, data);

        if (appointment.isPresent()) {
            LOGGER.info("A fost returnata resursa: " + appointment.get());
            return new ResponseEntity<>(new AppointmentHateoas().toModel(appointment.get()), HttpStatus.OK);
        } else {
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("parent");

            arrayLinks.add(parentLink);
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Resursa  nu exista");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id_patient}/{id_physician}/{data}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Appointment deleted!"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found!"
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
    public ResponseEntity<?> deleteAppointments(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id_patient, @PathVariable Long id_physician, @PathVariable String data) {
        try {
            LOGGER.info("Sa solicitat stergerea pentru: " + id_patient + " " + id_physician + " " + data);
            UserAuthorizationDto userAuthorizationDto ;
            try {
                userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
            }catch (PermissionDeniedException ex){
                return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
            }

            if(userAuthorizationDto == null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }else{
                if(userAuthorizationDto.getRole() == 2){
                    // trebuie sa fie decot
                    if(id_patient != userAuthorizationDto.getId()){
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(data, formatter);
            LocalDateTime currentDateTime = LocalDateTime.now();

             if(!dateTime.isAfter(currentDateTime)){
                 return new ResponseEntity<>(new ErrorDto("Nu se poarte șterge o programare din trecut!"),HttpStatus.CONFLICT);
             }
            parameterValidationsService.isValidDate(data);
            appointmentService.deleteAppointmentsByParameters(id_patient, id_physician, data);
            LOGGER.info("Stergerea a fost facuta cu succes");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException notFoundException) {
            LOGGER.info("Stergerea nu a putut fi efectuata! Resursa nu exista");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Data trebuie sa fie in formatul: yyyy-MM-dd HH:mm");
            return new ResponseEntity<>(new ErrorDto("Data trebuie sa fie in formatul: yyyy-MM-dd HH:mm"), HttpStatus.NOT_ACCEPTABLE);
        }
    }


    @PutMapping("/{id_patient}/{id_physician}/{data}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Appointment modified!"
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment created!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDTO.class))}
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
                            required = false,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<?> update(@RequestHeader(name="Authorization",required = false) String authorizationHeader,@PathVariable Long id_patient, @PathVariable Long id_physician, @PathVariable String data, @RequestBody AppointmentDTO appointmentDTO) throws NotAcceptableException {
        LOGGER.info("Sa solicitat modificarea pentru: " + id_patient + " " + id_physician + " " + data + " cu: " + appointmentDTO);
        // pentru logica aplicatiei fiecare programare dureaza 15 minute, deci pentru a mentine o ordine ss = 00, iar minutul 00,15,30,45
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }

        Optional<AppointmentDTO> appointmentDTOUpdate = appointmentService.selectAppointmentsByParameters(id_patient, id_physician, data);

        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 2 || userAuthorizationDto.getRole() ==1){
                // trebuie sa fie pacient si sa fie postarea lui, sau doctor care schimba statusul
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if(!Objects.equals(appointmentDTO.getId_patient(), id_patient) || !Objects.equals(appointmentDTO.getId_physician(), id_physician) || !Objects.equals(appointmentDTO.getDate(), data)){
            return new ResponseEntity<>(new ErrorDto("Cererea trebuie facuta cu aceleasi date si in url si in body."), HttpStatus.NOT_ACCEPTABLE);
        }

        LocalDateTime localDateTime;
        try {
            localDateTime = parameterValidationsService.isValidDate(data);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Data este invalida! Ea trebuie sa fie in formatul yyyy-MM-dd HH:mm");
            return new ResponseEntity<>(new ErrorDto("Data este invalida! Ea trebuie sa fie in formatul yyyy-MM-dd HH:mm"), HttpStatus.NOT_ACCEPTABLE);
        }
        if (!parameterValidationsService.isValidName(appointmentDTO.getStatus())) {
            LOGGER.error("Statusul poate contine doar caractere alfabetice");
            return new ResponseEntity<>(new ErrorDto("Statusul poate contine doar caractere alfabetice"), HttpStatus.NOT_ACCEPTABLE);
        }

        System.out.println(localDateTime.getMinute());
        if (localDateTime.getMinute() % 15 != 0) {
            LOGGER.error("Appointments can be scheduled at every hour and at 0, 15, 30, or 45 minutes past the hour.");
            return new ResponseEntity<>("{\"error\": \"" + "Appointments can be scheduled at every hour and at 0, 15, 30, or 45 minutes past the hour." + "\"}", HttpStatus.NOT_ACCEPTABLE);
        }
        LocalDateTime now = LocalDateTime.now();

        if (!localDateTime.isAfter(now.plus(15, ChronoUnit.MINUTES))) {
            LOGGER.error("Programarea poate fi facuta cu minim 15 minute dupa data si ora curenta.");
            return new ResponseEntity<>(new ErrorDto("Programarea poate fi facuta cu minim 15 minute dupa data si ora curenta."), HttpStatus.NOT_ACCEPTABLE);
        }
        if(!Objects.equals(appointmentDTO.getDate(), data) && !Objects.equals(appointmentDTO.getId_physician(), id_physician) && !Objects.equals(appointmentDTO.getId_patient(), id_patient)){
            return new ResponseEntity<>(new ErrorDto("Datele din URL nu sunt aceleasi cu cele din body!"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (appointmentDTOUpdate.isPresent()) {
            LOGGER.info("Resursa este prezenta deci se face update");

            if (appointmentDTOUpdate.get().getId_patient() != userAuthorizationDto.getId() && appointmentDTOUpdate.get().getId_physician() != userAuthorizationDto.getId()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            appointmentDTOUpdate.get().setStatus(appointmentDTO.getStatus());
            try {
                appointmentService.updateAppointmentsByParameters(id_patient, id_physician, data, appointmentDTOUpdate.get());
            } catch (NotAcceptableException notAcceptableException) {
                LOGGER.error(notAcceptableException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.NOT_ACCEPTABLE);
            }
            LOGGER.info("Modificarea a fost facuta cu succes");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            try {

                if(userAuthorizationDto == null){
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }else{
                    if(userAuthorizationDto.getRole() == 2){
                        // trebuie sa fie decot
                        if(id_patient != userAuthorizationDto.getId()){
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }
                    }else{
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
                LOGGER.info("Resursa nu exista se face insert");

                appointmentService.createAppointmentsByParameters(appointmentDTO);
                LOGGER.info("Resursa a fost adaugata cu succes");
                return new ResponseEntity<>(new AppointmentHateoas().toModel(appointmentDTO), HttpStatus.CREATED);
            } catch (NotAcceptableException notAcceptableException) {
                LOGGER.error(notAcceptableException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.NOT_ACCEPTABLE);
            }
        }
    }

    ///api/medical_office/physicians/{id}/patients
    @GetMapping("/physicians/{id}/patients")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPhysicians(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id) {

        LOGGER.info("Sau solicitat programarile doctorului cu id-ul: " + id);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }

        System.out.print(authorizationHeader);
        if(userAuthorizationDto == null){
            LOGGER.error("Acces neautorizat header inexistent");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() != 1){
                LOGGER.error("Acces neautorizat, nu este doctor");
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // daca e doctor si cauta programarile lui le returnez
        List<AppointmentDTO> appointments = appointmentService.selectAppointmentsByIdPhysician(id);
        if (!appointments.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : appointments
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("Sa returnat o lista nevida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Lista de programari este vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }

    }

    @GetMapping("/patients/{id}/physicians")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPatient(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id) {
        LOGGER.info("Sau solicitat programarile pacientului cu id-ul: " + id);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }


        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 2){
                // trebuie sa fie decor
                if(id != userAuthorizationDto.getId()){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else if(userAuthorizationDto.getRole() != 1)
            {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        List<AppointmentDTO> appointments = appointmentService.selectAppointmentsByIdPatient(id);
        if (!appointments.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : appointments
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("A fost returnata o lista nevida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("A fost returnata o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }

    @GetMapping("/page")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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

    public ResponseEntity<?> getAllPatientsPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "items_per_page", defaultValue = "10") int itemsPerPage
    ) {
        LOGGER.info("Sa solicitat o afisare paginata cu nrPagina:" + page + " itemi pe pagina: " + itemsPerPage);
        // metoda nu este expusa!
        if(true)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        try {
            List<AppointmentDTO> patientDTOList = appointmentService.getAllAppointmentsPage(page, itemsPerPage);
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : patientDTOList
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("A fost returnata o lista de programari");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NotAcceptableException notAcceptableException) {
            LOGGER.info(notAcceptableException.getMessage());
            return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/patients/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPatientAndDate(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id, @RequestParam TypeSelectDate type, @RequestParam String value) {
        LOGGER.info("S-au solicitat datele pacientului" + id +  " din data: " + value + " cu formatul: " + type);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }


        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 2){
                // trebuie sa fie decot
                if(id != userAuthorizationDto.getId()){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        try {
            List<AppointmentDTO> appointments = appointmentService.selectAppointmentsByIdPatientAndDate(id, type, value);
            if (!appointments.isEmpty()) {
                List<EntityModel> list = new ArrayList<>();
                for (AppointmentDTO p : appointments
                ) {
                    list.add(new AppointmentHateoas().toModel(p));
                }
                LOGGER.info("A fost returnata o lista nevida");
                return new ResponseEntity<>(list, HttpStatus.OK);
            } else {
                // nu exista doar link spre parinte
                Map<String, ArrayList<Link>> links = new HashMap<>();
                ArrayList<Link> arrayLinks = new ArrayList<>();
                Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

                arrayLinks.add(parentLink);
                links.put("result", new ArrayList<>());
                links.put("_links", new ArrayList<>(arrayLinks));
                LOGGER.info("A fost returnata o lista vida");
                return new ResponseEntity<>(links, HttpStatus.OK);
            }
        } catch (NotAcceptableException notAcceptableException) {
            LOGGER.error(notAcceptableException.getMessage());
            return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/physicians/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPhysiciansAndDate(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id, @RequestParam TypeSelectDate type, @RequestParam String value) {
        LOGGER.info("S-au solicitat datele medicului" + id +  " din data: " + value + " cu formatul: " + type);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }


        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 1){
                // trebuie sa fie decot
                if(id != userAuthorizationDto.getId()){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        List<AppointmentDTO> appointments;
        try {
            appointments = appointmentService.selectAppointmentsByIdPhysiciansAndDate(id, type, value);
        } catch (NotAcceptableException notAcceptableException) {
            LOGGER.error(notAcceptableException.getMessage());
            return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
        if (!appointments.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : appointments
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("A fost retunata o lista nevida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("A fost returnata o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }

    @GetMapping("/physicians/{id}/status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPhysiciansAndStatus(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id, @RequestParam String status) {
        LOGGER.info("Sau solicitat programarile medicului cu id-ul: " + id + " si statusul: " + status);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }


        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 1){
                // trebuie sa fie decot
                if(id != userAuthorizationDto.getId()){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (!parameterValidationsService.isValidName(status)) {
            LOGGER.error("Statusul poate contine doar caractere alfabetice!");
            return new ResponseEntity<>(new ErrorDto("Statusul poate contine doar caractere alfabetice!"), HttpStatus.NOT_ACCEPTABLE);
        }
        List<AppointmentDTO> appointments = appointmentService.selectAppointmentsByIdPhysiciansAndStatus(id, status);
        if (!appointments.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : appointments
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("A fost returnata o lista nevida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("A fost returnata o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }

    @GetMapping("/patient/{id}/status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPatientAndStatus(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id, @RequestParam String status) {
        LOGGER.info(" A fosr solicitata lista pacientului cu id-ul: " + id + " si statusul: " + status);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }


        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() == 2){
                // trebuie sa fie decot
                if(id != userAuthorizationDto.getId()){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (!parameterValidationsService.isValidName(status)) {
            LOGGER.info("Statusul nu poate contine decat caractere numerice!");
            return new ResponseEntity<>(new ErrorDto("Statusul nu poate contine decat caractere numerice!"), HttpStatus.NOT_ACCEPTABLE);
        }
        List<AppointmentDTO> appointments = appointmentService.selectAppointmentsByIdPatientAndStatus(id, status);
        if (!appointments.isEmpty()) {
            List<EntityModel> list = new ArrayList<>();
            for (AppointmentDTO p : appointments
            ) {
                list.add(new AppointmentHateoas().toModel(p));
            }
            LOGGER.info("A fosr returnata o lista nevida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getAllAppointments()).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("A fosr returnata o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
    }

    @GetMapping("/physicians/{id}/patientsId")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))
                    }
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
    public ResponseEntity<?> getByIdPhysiciansPatients(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long id) {

        LOGGER.info("Sau solicitat pacienti doctorului cu id-ul: " + id);
        UserAuthorizationDto userAuthorizationDto ;
        try {
            userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        }catch (PermissionDeniedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()),HttpStatus.UNAUTHORIZED);
        }

        System.out.print(authorizationHeader);
        if(userAuthorizationDto == null){
            LOGGER.error("Acces neautorizat header inexistent");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() != 1){
                LOGGER.error("Acces neautorizat, nu este doctor");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // daca e doctor si cauta programarile lui le returnez
        List<AppointmentDTO> appointments = appointmentService.selectAppointmentsByIdPhysician(id);
        if (!appointments.isEmpty()) {
            Set<String> idSet = new HashSet<>();
            for (AppointmentDTO p : appointments) {
                idSet.add(p.getId_patient().toString());
            }

            List<String> list = new ArrayList<>(idSet);
            LOGGER.info("Sa returnat o lista nevida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(AppointmentsController.class).getByIdPhysiciansPatients(authorizationHeader,id)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Lista de pacienti este vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }

    }
}
