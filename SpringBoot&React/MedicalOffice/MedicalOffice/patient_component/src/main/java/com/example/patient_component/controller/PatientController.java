package com.example.patient_component.controller;


import com.example.patient_component.dto.*;
import com.example.patient_component.exceptions.InvalidPageNumber;
import com.example.patient_component.exceptions.NotAcceptableException;
import com.example.patient_component.hateoas.PatientHateoas;
import com.example.patient_component.services.AuthorizationService;
import com.example.patient_component.services.ParameterValidationsService;
import com.example.patient_component.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.Link;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/pos/patients")
public class PatientController {
    @Autowired
    PatientService patientService;
    @Autowired
    ParameterValidationsService parameterValidationsService;
    @Autowired
    AuthorizationService authorizationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientController.class);

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class)))
                    }
            ),
            @ApiResponse(
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
    public ResponseEntity<?> getAllPatients(@RequestHeader("Authorization") String authorizationHeader) {
        LOGGER.info("S-au solicitat toti pacienti");
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto != null) {
            if (userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                List<PatientDTO> patients = patientService.findAll();
                if (!patients.isEmpty()) {
                    List<EntityModel> list = new ArrayList<>();
                    for (PatientDTO p : patients
                    ) {
                        // pentru fiecare informatii de legatura
                        list.add(new PatientHateoas().toModel(new Pair<>(authorizationHeader, p)));
                    }
                    LOGGER.info("A fost returnata o lista nevida de pacienti");
                    return new ResponseEntity<>(list, HttpStatus.OK);
                } else {
                    // nu exista doar link spre parinte
                    Map<String, ArrayList<Link>> links = new HashMap<>();
                    ArrayList<Link> arrayLinks = new ArrayList<>();
                    Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("self");

                    arrayLinks.add(parentLink);
                    links.put("result", new ArrayList<>());
                    links.put("_links", new ArrayList<>(arrayLinks));
                    LOGGER.info("A fost returnata o lista vida de pacienti");
                    return new ResponseEntity<>(links, HttpStatus.OK);
                }
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{cnp}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PatientDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Patient not found!"
            ),
            @ApiResponse(
                    responseCode = "422", description = "Valoare invalida pentru cnp!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
            ),
            @ApiResponse(
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
    public ResponseEntity<?> getPatientById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String cnp) {
        LOGGER.info("Sa solicitat pacientul  cu cnp-ul: " + cnp);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            // verific daca e pacientul cu acel cnp sau medic
            if(!parameterValidationsService.isValidCNP(cnp)){
                return new ResponseEntity<>(new ErrorDto("CNP invalid!"), HttpStatus.UNPROCESSABLE_ENTITY);
            }
            Optional<PatientDTO> patientDTO = this.patientService.findByCnp(cnp);
            if (!patientDTO.isPresent()) {
                // nu exista doar link spre parinte
                Map<String, ArrayList<Link>> links = new HashMap<>();
                ArrayList<Link> arrayLinks = new ArrayList<>();
                Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent");

                arrayLinks.add(parentLink);
                links.put("_links", new ArrayList<>(arrayLinks));
                LOGGER.info("Pacietul nu a fost gasit!");
                return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
            }
            // este doctor sau pacientul cu ecel id
            if (userAuthorizationDto.getRole() == 1 || patientDTO.get().getUser_id() == userAuthorizationDto.getId()) {
                Optional<PatientDTO> patient = patientService.findByCnp(cnp);

                // daca exista un rezultat
                if (!patient.isEmpty()) {
                    // 2 link-uri (self + parent), elementul
                    LOGGER.info("A fost gasit pacientul");
                    // header folosit pentru cereri spre celelalte micrsoervicii(validare ca are programari de exemplu)
                    return new ResponseEntity<>(new PatientHateoas().toModel(new Pair<>(authorizationHeader, patient.get())), HttpStatus.OK);
                } else {
                    // nu exista doar link spre parinte
                    Map<String, ArrayList<Link>> links = new HashMap<>();
                    ArrayList<Link> arrayLinks = new ArrayList<>();
                    Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent");

                    arrayLinks.add(parentLink);
                    links.put("_links", new ArrayList<>(arrayLinks));
                    LOGGER.info("Pacietul nu a fost gasit!");
                    return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
    }

    // metoda are send pentru ca la login, nu se returneaza id-ul utilizatorului(cnp) din tabela paienti ci cel din user
    @GetMapping("/id_patient/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PatientDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Patient not found!"
            ),
            @ApiResponse(
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
    // parametru prin url
    public ResponseEntity<?> getPatientByIdNr(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        LOGGER.info("Sa solicitat pacientul cu id-ul: " + id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getId() != id && userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        Optional<PatientDTO> patient = patientService.findById(id);

        // daca exista un rezultat
        if (!patient.isEmpty()) {
            // 2 link-uri (self + parent), elementul
            LOGGER.info("A fost returnat pacientul");
            return new ResponseEntity<>(new PatientHateoas().toModel(new Pair<>(authorizationHeader, patient.get())), HttpStatus.OK);
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent");

            arrayLinks.add(parentLink);
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Pacientul nu a fost gasit");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    // 2xx mereu chiar daca nu e
    @DeleteMapping("/{cnp}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Patient deleted!"
            ),
            @ApiResponse(
                    responseCode = "404", description = "Patient not found!"
            ),
            @ApiResponse(
                    responseCode = "422", description = "Valoare invalida pentru cnp!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
            ),
            @ApiResponse(
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
    public ResponseEntity<?> deletePatient(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String cnp) {
        LOGGER.info("Sa solicitat stergerea pacietului cu cnp-ul: " + cnp);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            Optional<PatientDTO> patientDTO = this.patientService.findByCnp(cnp);
            if (patientDTO.isPresent()) {
                if (patientDTO.get().getUser_id() != userAuthorizationDto.getId()) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        // e token valid are acelasi cnp cu care vreau sa sterg
        if (!parameterValidationsService.isValidCNP(cnp)) {
            LOGGER.info("CNP in format invalid!");
            return new ResponseEntity<>(new ErrorDto("CNP in format invalid!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Optional<PatientDTO> patient = patientService.findByCnp(cnp);
        if (patient.isPresent()) {
            if(patient.get().getIs_active()) {
                patientService.deactivateByCnp(cnp);
                LOGGER.info("Stergerea a fost facuta cu succes");
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }else{
                // resursa e inaccesibila
                return new ResponseEntity<>(new ErrorDto("Contul a fost dezactivat! Nu mai poate fi accesat"), HttpStatus.NOT_FOUND);
            }
        } else {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("parent");

            arrayLinks.add(parentLink);

            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Pacientul nu poate fi sters pentru ca nu exista");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{cnp}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Patient updated!"
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Patient created!",
                    // se returneaza reprezentarea
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PatientDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict!"
            ),@ApiResponse(
            responseCode = "422", description = "Valoare invalida pentru cnp!",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
    ),   @ApiResponse(
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
    public ResponseEntity<?> patientUpdate(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @PathVariable String cnp, @RequestBody PatientDTO patientDTO) {
        LOGGER.info("Sa solicitat modificarea/adaugarea pacientului cu cnp-ul:" + cnp + " si datele: " + patientDTO);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        Optional<PatientDTO> patientDTO1 = this.patientService.findByCnp(cnp);
        if (patientDTO1.isPresent()) {
            // se face update doar daca are acel id si e pacient
            if (userAuthorizationDto == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userAuthorizationDto.getId() != patientDTO1.get().getUser_id() || userAuthorizationDto.getRole() != 2) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // e pacient cu acel cnp sau nu exista resursa
        // daca e autentificat si medic e liber
        if (patientDTO.getLastName().length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (patientDTO.getFirstName().length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (patientDTO.getEmail().length() > 70) {
            LOGGER.info("Adresa de email nu poate avea mai mult de 70 de caractere");
            return new ResponseEntity<>(new ErrorDto("Adresa de email nu poate avea mai mult de 70 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validare
        if (!parameterValidationsService.isValidCNP(cnp)) {
            LOGGER.info("CNP invalid!");
            return new ResponseEntity<>(new ErrorDto("CNP invalid!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidCNP(patientDTO.getCnp())) {
            LOGGER.info("CNP invalid!");
            return new ResponseEntity<>(new ErrorDto("CNP invalid!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        patientDTO.setCnp(cnp);
        if (!parameterValidationsService.isValidName(patientDTO.getFirstName())) {
            LOGGER.info("Prenume invalid!");
            return new ResponseEntity<>(new ErrorDto("Prenume invalid!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidName(patientDTO.getLastName())) {
            LOGGER.info("Nume de familie invalid!");
            return new ResponseEntity<>(new ErrorDto("Nume de familie invalid!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            LocalDate localDate = parameterValidationsService.isValidDate(patientDTO.getBirthDay().toString());
        } catch (Exception ex) {
            LOGGER.info("Data nu este in formatul yyyy-MM-dd");
            return new ResponseEntity<>(new ErrorDto("Data nu este in formatul yyyy-MM-dd"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidTelephone(patientDTO.getTelephone())) {
            LOGGER.info("Numar de telefon invalid");
            return new ResponseEntity<>(new ErrorDto("Numar de telefon invalid"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidEmail(patientDTO.getEmail())) {
            LOGGER.info("Adresa de email invalida!");
            return new ResponseEntity<>(new ErrorDto("Adresa de email invalida!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            boolean httpStatus = patientService.updateOrAdd(cnp, patientDTO);
            if (!httpStatus) {
                LOGGER.info("Pacientul a fost creat");
                return new ResponseEntity<>(new PatientHateoas().toModel(new Pair<>(authorizationHeader, patientDTO)), HttpStatus.CREATED);
            } else {
                LOGGER.info("Pacientul a fost modificat");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }


        } catch (NotAcceptableException notAcceptableException) {
            LOGGER.error(notAcceptableException.getMessage());
            return new ResponseEntity<>(new ErrorDto(notAcceptableException.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.CONFLICT);
        }
    }


    @GetMapping("/name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class)))
                    }
            ),@ApiResponse(
            responseCode = "422", description = "Valoare invalida pentru cnp!",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
    ),
            @ApiResponse(
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
    public ResponseEntity<?> getAllPatientsByName(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String name) {
        LOGGER.info("Sa solicitat lista pacientilor cu numele: " + name);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (name.length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!parameterValidationsService.isValidName(name)) {
            LOGGER.info("Nume invalid! Acesta poate contine doar caractere alfabetice!");
            return new ResponseEntity<>(new ErrorDto("Nume invalid! Acesta poate contine doar caractere alfabetice!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<PatientDTO> result = patientService.getAllPatientsByName(name);
        if (result.isEmpty()) {
            {
                // nu exista doar link spre parinte
                Map<String, ArrayList<Link>> links = new HashMap<>();
                ArrayList<Link> arrayLinks = new ArrayList<>();
                Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("self");

                arrayLinks.add(parentLink);
                links.put("result", new ArrayList<>());
                links.put("_links", new ArrayList<>(arrayLinks));
                LOGGER.info("A fost returnata o lista nevida");
                return new ResponseEntity<>(links, HttpStatus.OK);
            }
        } else {
            List<EntityModel> list = new ArrayList<>();
            for (PatientDTO p : result
            ) {
                list.add(new PatientHateoas().toModel(new Pair<>(authorizationHeader, p)));
            }
            LOGGER.info("A fosr returnata o lista vida");
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping("/birthDate")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class)))
                    }
            ), @ApiResponse(
            responseCode = "422", description = "Valoare invalida pentru cnp!",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
    ),
            @ApiResponse(
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
    public ResponseEntity<?> getAllPatientsByBirthDate(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String birthDateStr) {
        LOGGER.info("Sau solicitat pacienti nascuti la data: " + birthDateStr);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // daca e autentificat si medic e liber
        String expectedFormat = "yyyy-MM-dd";
        LocalDate birthDate;
        try {
            birthDate = parameterValidationsService.isValidDate(birthDateStr);
        } catch (Exception ex) {
            LOGGER.info("Data nu este in formatul yyyy-MM-dd");
            return new ResponseEntity<>(new ErrorDto("Data nu este in formatul yyyy-MM-dd"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        List<PatientDTO> result = patientService.getAllPatientsByBirthDate(birthDate);
        if (result.isEmpty()) {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        } else {
            List<EntityModel> list = new ArrayList<>();
            for (PatientDTO p : result
            ) {
                list.add(new PatientHateoas().toModel(new Pair<>(authorizationHeader, p)));
            }
            LOGGER.info("Sa returnat o lista nevida!");
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    @GetMapping("/isActive")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class)))

                    }
            ),
            @ApiResponse(
                    responseCode = "401", description = "Token invalid!"
            ),
            @ApiResponse(
                    responseCode = "403", description = "Nu exista permisiuni pentru a accesa resursa!"
            ),})
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
    public ResponseEntity<?> getAllPatientsActives(@RequestHeader("Authorization") String authorizationHeader) {
        LOGGER.info("Sau solictitat pacienti activi");
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // daca e autentificat si medic e liber
        List<PatientDTO> list = patientService.findByIsActiveTrue();

        if (list.isEmpty()) {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PatientController.class).getAllPatients(null)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("A fost returnata o lista vida de pacienti!");
            return new ResponseEntity<>(links, HttpStatus.OK);

        } else {
            List<EntityModel> list2 = new ArrayList<>();
            for (PatientDTO p : list
            ) {
                list2.add(new PatientHateoas().toModel(new Pair<>(authorizationHeader, p)));
            }
            LOGGER.info("A fost returnata o lista nevida de pacienti");
            return new ResponseEntity<>(list2, HttpStatus.OK);
        }
    }

    @GetMapping("/page")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class)))
                    }
            ),
            @ApiResponse(
                    responseCode = "416", description = "Requested Range Not Satisfiable"
            ),
            @ApiResponse(
                    responseCode = "422", description = "Valoare invalida pentru cnp!",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
            ),   @ApiResponse(
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
    public ResponseEntity<?> getAllPatientsPage(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                @RequestParam(name = "items_per_page", defaultValue = "10") int itemsPerPage
    ) {
        LOGGER.info("Sau solicititata pacienti de pe pagina: " + page + " cu numarul de itemi: " + itemsPerPage);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (userAuthorizationDto.getRole() != 1) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // daca e autentificat si medic e liber
        if (itemsPerPage <= 0 || itemsPerPage >= 100 || page < 0) {
            LOGGER.info("Valoare invalida pentru datele de paginare. Trebuie sa fie mai mari ca 0, iar numarul de itemi pe pagina mai mic decat 100");
            return new ResponseEntity<>(new ErrorDto("Valoare invalida pentru datele de paginare. Trebuie sa fie mai mari ca 0, iar numarul de itemi pe pagina mai mic decat 100"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            List<PatientDTO> patientDTOList = patientService.getAllPatientsPage(page, itemsPerPage);
            List<EntityModel> list = new ArrayList<>();
            for (PatientDTO p : patientDTOList
            ) {
                list.add(new PatientHateoas().toModel(new Pair<>(authorizationHeader, p)));
            }
            LOGGER.info("A fost returnata lista de valori ceruta");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (InvalidPageNumber invalidPageNumber) {
            LOGGER.error(invalidPageNumber.getMessage());
            return new ResponseEntity<>(new ErrorDto(invalidPageNumber.getMessage()), HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Valoarea unui camp este invalida! Ele nu pot fi negative");
            return new ResponseEntity<>(new ErrorDto("Valoarea unui camp este invalida! Ele nu pot fi negative"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    // metoda este folosita pentru pacienti unui doctor(agregare mai rapida)
    @PostMapping("/listPatients")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients found!",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class)))
                    }
            ),   @ApiResponse(
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
    public ResponseEntity<?> getAllPatientsById(@RequestHeader("Authorization") String authorizationHeader,@RequestBody ListId identificatori) {
        LOGGER.info("S-au solicitat toti pacienti");
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if (userAuthorizationDto != null) {
            if (userAuthorizationDto.getRole() != 1) {
                // nu este doctor
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                List<PatientDTO> patients = patientService.findAll();
                if (!patients.isEmpty()) {
                    List<ShortPatientDto> list = new ArrayList<>();
                    for (PatientDTO p : patients
                    ) {
                        boolean ok = false;
                        for(int i=0;i<identificatori.getList().size();i++){
                            if(p.getUser_id().toString().equals(identificatori.getList().get(i))){
                                ok = true;
                            }
                        }
                        if(ok){
                            list.add(new ShortPatientDto(p.getUser_id(),p.getFirstName(),p.getLastName(),p.getEmail(),p.getTelephone()));
                        }
                    }
                    LOGGER.info("A fost returnata o lista nevida de pacienti");
                    return new ResponseEntity<>(list, HttpStatus.OK);
                } else {
                    // nu exista doar link spre parinte
                    Map<String, ArrayList<Link>> links = new HashMap<>();
                    ArrayList<Link> arrayLinks = new ArrayList<>();
                    Link parentLink = linkTo(methodOn(PatientController.class).getAllPatientsById(null,identificatori)).withRel("self");

                    arrayLinks.add(parentLink);
                    links.put("result", new ArrayList<>());
                    links.put("_links", new ArrayList<>(arrayLinks));
                    LOGGER.info("A fost returnata o lista vida de pacienti");
                    return new ResponseEntity<>(links, HttpStatus.OK);
                }

            }

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
