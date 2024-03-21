package com.example.physician_component.controller;

import com.example.physician_component.dto.*;
import com.example.physician_component.exceptions.ConflictException;
import com.example.physician_component.exceptions.InvalidPageNumber;
import com.example.physician_component.exceptions.NotAcceptedException;
import com.example.physician_component.hateoas.PhysiciansHateoas;
import com.example.physician_component.service.AuthorizationService;
import com.example.physician_component.service.ParameterValidationsService;
import com.example.physician_component.service.PhysiciansService;
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

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/pos/physicians")
public class PhysiciansController {
    @Autowired
    private PhysiciansService physiciansService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    private ParameterValidationsService parameterValidationsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhysiciansController.class);

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physicians found!", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PhysiciansDTO.class)))
            }),   @ApiResponse(
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
    public ResponseEntity<?> getAllPhysician(@RequestHeader("Authorization") String authorizationHeader) {
        LOGGER.info("Sau solicitat toti pacienti");
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else{
            if(userAuthorizationDto.getRole() == 0){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        List<PhysiciansDTO> physicians = physiciansService.findAll();
        if (physicians.isEmpty()) {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o lista vida de doctori.");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
        List<EntityModel> list = new ArrayList<>();
        for (PhysiciansDTO p : physicians) {
            list.add(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,p)));
        }
        LOGGER.info("Sa returnat o lista nevida de doctori.");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physician found!", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PhysiciansDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Physician not found!"),
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
    public ResponseEntity<?> getPhysicianById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        LOGGER.info("Sa solicitat doctorul cu id-ul: " + id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else{
            if(userAuthorizationDto.getRole() != 1 && userAuthorizationDto.getRole() != 2){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        Optional<PhysiciansDTO> physician = physiciansService.findById(id);

        if (physician.isPresent()) {
            LOGGER.info("Doctorul exista si a fost returnat");
            if(physician.get().getId_user() != userAuthorizationDto.getId()){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,physician.get())), HttpStatus.OK);
        } else {
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("parent");

            arrayLinks.add(parentLink);
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Doctorul nu exista!");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    // METODA APARE DEOARECE LA LOGIN NU POATE FI ACCESAT MEDICUL DUPA ID-UL la LOGIN(aceste nu e legat de idm) deci cumva trebuie asocierea fie in idm se returneaza id de doctor
    // fie se mai pune un get aici
    @GetMapping("/id_user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physician found!", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PhysiciansDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Physician not found!"),
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
    public ResponseEntity<?> getPhysicianByIdUser(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        LOGGER.info("Sa solicitat doctorul cu id-ul: " + id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else{
            if(userAuthorizationDto.getRole() != 1 && userAuthorizationDto.getRole() != 2){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        Optional<PhysiciansDTO> physician = physiciansService.findByIdUser(id);

        if (physician.isPresent()) {
            if(physician.get().getId_user() != userAuthorizationDto.getId()){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            LOGGER.info("Doctorul exista si a fost returnat");
            return new ResponseEntity<>(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,physician.get())), HttpStatus.OK);
        } else {
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("parent");

            arrayLinks.add(parentLink);
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Doctorul nu exista!");
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Physician created!", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PhysiciansDTO.class))}),
            @ApiResponse(responseCode = "422", description = "Invalid values!"),
            @ApiResponse(responseCode = "409", description = "Valorile introduse cauzeaza conflicte cu alte resurse!"),
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
    public ResponseEntity<?> createPhysicians(@RequestHeader("Authorization") String authorizationHeader,@RequestBody PhysiciansDTO physicianDTO) {
        LOGGER.info("Sa solicitat crearea doctorului cu datele: " + physicianDTO);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            if(userAuthorizationDto.getRole() != 0){
                // doar adminul poate crea doctor
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (physicianDTO.getLast_name().length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (physicianDTO.getFirst_name().length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (physicianDTO.getEmail().length() > 70) {
            LOGGER.info("Adresa de email nu poate avea mai mult de 70 de caractere");
            return new ResponseEntity<>(new ErrorDto("Adresa de email nu poate avea mai mult de 70 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidName(physicianDTO.getFirst_name())) {
            LOGGER.error("Prenumele nu este valid!");
            return new ResponseEntity<>(new ErrorDto("Prenumele nu este valid!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidName(physicianDTO.getLast_name())) {
            LOGGER.error("Numele nu este valid!");
            return new ResponseEntity<>(new ErrorDto("Numele nu este valid!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidEmail(physicianDTO.getEmail())) {
            LOGGER.error("Adresa de email nu este valida!");
            return new ResponseEntity<>(new ErrorDto("Adresa de email nu este valida!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidTelephone(physicianDTO.getTelephone())) {
            LOGGER.error("Numar de telefon invalid!");
            return new ResponseEntity<>(new ErrorDto("Numar de telefon invalid!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            PhysiciansDTO savedPhysician = physiciansService.save(physicianDTO);
            LOGGER.info("Doctorul a fost creat");
            return new ResponseEntity<>(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,savedPhysician)), HttpStatus.CREATED);
        } catch (NotAcceptedException notAcceptedException) {
            LOGGER.info(notAcceptedException.getMessage());
            return new ResponseEntity<>(new ErrorDto(notAcceptedException.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ConflictException conflictException) {
            LOGGER.info(conflictException.getMessage());
            return new ResponseEntity<>(new ErrorDto(conflictException.getMessage()),
                    HttpStatus.CONFLICT);
        }
    }


    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Physician updated!"),
            @ApiResponse(responseCode = "201", description = "Physician created!",
                    // se returneaza reprezentarea
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = PhysiciansDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Valorile introduse cauzeaza conflicte cu alte resurse!"),
            @ApiResponse(responseCode = "422", description = "Invalid values!"),
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
    public ResponseEntity<?> physicianUpdate(@RequestHeader(name = "Authorization",required = false) String authorizationHeader,@PathVariable Long id, @RequestBody PhysiciansDTO physicianDTO) {
        LOGGER.info("Sa solicitat actualizarea medicului cu id-ul: " + id + " cu noul continut: " + physicianDTO);
        Optional<PhysiciansDTO> physiciansDTO = this.physiciansService.findById(id);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(physiciansDTO.isPresent()){
            // se face update doar de ace user
            if(userAuthorizationDto.getId() != physiciansDTO.get().getId_user()){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (physicianDTO.getLast_name().length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (physicianDTO.getFirst_name().length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (physicianDTO.getEmail().length() > 70) {
            LOGGER.info("Adresa de email nu poate avea mai mult de 70 de caractere");
            return new ResponseEntity<>(new ErrorDto("Adresa de email nu poate avea mai mult de 70 de caractere"), HttpStatus.UNPROCESSABLE_ENTITY);
        }


        if (!parameterValidationsService.isValidName(physicianDTO.getFirst_name())) {
            LOGGER.info("Prenumele nu este valid!");
            return new ResponseEntity<>(new ErrorDto("Prenumele nu este valid!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidName(physicianDTO.getLast_name())) {
            LOGGER.info("Numele nu este valid!");
            return new ResponseEntity<>(new ErrorDto("Numele nu este valid!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidEmail(physicianDTO.getEmail())) {
            LOGGER.info("Adresa de email nu este valida!");
            return new ResponseEntity<>(new ErrorDto("Adresa de email nu este valida!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!parameterValidationsService.isValidTelephone(physicianDTO.getTelephone())) {
            LOGGER.info("Numar de telefon invalid!");
            return new ResponseEntity<>(new ErrorDto("Numar de telefon invalid!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Optional<PhysiciansDTO> physician = physiciansService.findById(id);

        if (physician.isPresent()) {
            try {
                physiciansService.update(id, physicianDTO);
                LOGGER.info("Dactorul a fost modificat cu succs");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotAcceptedException notAcceptedException) {
                LOGGER.info(notAcceptedException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptedException.getMessage()),

                        HttpStatus.UNPROCESSABLE_ENTITY);
            } catch (ConflictException conflictException) {
                LOGGER.info(conflictException.getMessage());
                return new ResponseEntity<>(new ErrorDto(conflictException.getMessage()),
                        HttpStatus.CONFLICT);
            }
        } else {
            try {
                if(userAuthorizationDto.getRole() != 0){
                    // nu e admin nu poate face insert
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                physiciansService.save(physicianDTO);
                LOGGER.info("Nu exista doctorul asa ca a fost creat unul nou");
                return new ResponseEntity<>(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,physicianDTO)), HttpStatus.CREATED);
            } catch (NotAcceptedException notAcceptedException) {
                LOGGER.info(notAcceptedException.getMessage());
                return new ResponseEntity<>(new ErrorDto(notAcceptedException.getMessage()),
                        HttpStatus.UNPROCESSABLE_ENTITY);
            } catch (ConflictException conflictException) {
                LOGGER.info(conflictException.getMessage());
                return new ResponseEntity<>(new ErrorDto(conflictException.getMessage()),
                        HttpStatus.CONFLICT);
            }
        }
    }

    @GetMapping("/specialization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physicians found!", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PhysiciansDTO.class)))
            }),
            @ApiResponse(
                    responseCode = "406", description = "Specializare invalida!"
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
    public ResponseEntity<?> getAllPhysicianBySpecialization(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String specialization) {
        LOGGER.info("Sau solicitat medici cu specializarea: " + specialization);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else{
            if(userAuthorizationDto.getRole() != 1 && userAuthorizationDto.getRole() != 2){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (!parameterValidationsService.isValidSpecialization(specialization)) {
            LOGGER.info("Specializare invalida! Poate contine doar litere!");
            return new ResponseEntity<>(new ErrorDto("Specializare invalida! Poate contine doar litere!"), HttpStatus.NOT_ACCEPTABLE);
        }
        List<PhysiciansDTO> s = physiciansService.selectPhysiciansBySpecialization(specialization);
        if (s.isEmpty()) {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPhysicianBySpecialization(null,specialization)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
        List<EntityModel> list = new ArrayList<>();
        for (PhysiciansDTO p : s) {
            list.add(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,p)));
        }
        LOGGER.info("Sa returnat o lista cu noi date");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physicians found!", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PhysiciansDTO.class)))
            }),
            @ApiResponse(responseCode = "406", description = "Numele dupa care se face cautarea este invalid!"),
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
    public ResponseEntity<?> getAllPhysicianByName(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String name) {
        LOGGER.info("Sau solicitat pacienti cu numele: " + name);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else{
            if(userAuthorizationDto.getRole() != 1 && userAuthorizationDto.getRole() != 2){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (name.length() > 50) {
            LOGGER.info("Numele nu poate avea mai mult de 50 de caractere");
            return new ResponseEntity<>(new ErrorDto("Numele nu poate avea mai mult de 50 de caractere"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (!parameterValidationsService.isValidName(name)) {
            LOGGER.error("Numele poate contine doar caractere numerice!");
            return new ResponseEntity<>(new ErrorDto("Numele poate contine doar caractere numerice!"), HttpStatus.NOT_ACCEPTABLE);
        }
        List<PhysiciansDTO> result = physiciansService.findByName(name);
        if (result.isEmpty()) {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPhysicianByName(null,name)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o lista vida");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
        List<EntityModel> list = new ArrayList<>();
        for (PhysiciansDTO p : result) {
            list.add(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,p)));
        }
        LOGGER.info("Sa returnat o lista cu elemente");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physicians found!", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PhysiciansDTO.class)))
            }),
            @ApiResponse(
                    responseCode = "416", description = "Requested Range Not Satisfiable",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}
            ),
            @ApiResponse(
                    responseCode = "422", description = "Valori pentru paginare invalide!"
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
    public ResponseEntity<?> getAllPatientsPage(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "items_per_page", defaultValue = "10") int itemsPerPage) {
        LOGGER.info("Sa solicitat pagina cu nr: " + page + " cu nr itemi pe pagina: " + itemsPerPage);
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else{
            if(userAuthorizationDto.getRole() != 1 && userAuthorizationDto.getRole() != 2){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        if (itemsPerPage <= 0 || itemsPerPage >= 100 || page < 0) {
            LOGGER.info("Valoare invalida pentru datele de paginare. Trebuie sa fie mai mari ca 0, iar numarul de itemi pe pagina mai mic decat 100");
            return new ResponseEntity<>(new ErrorDto("Valoare invalida pentru datele de paginare. Trebuie sa fie mai mari ca 0, iar numarul de itemi pe pagina mai mic decat 100"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            List<PhysiciansDTO> physicianDTOList = physiciansService.getAllPhysiciansPage(page, itemsPerPage);
            if (physicianDTOList.isEmpty()) {
                // nu exista doar link spre parinte
                Map<String, ArrayList<Link>> links = new HashMap<>();
                ArrayList<Link> arrayLinks = new ArrayList<>();
                Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPatientsPage(null,page, itemsPerPage)).withRel("self");

                arrayLinks.add(parentLink);
                links.put("result", new ArrayList<>());
                links.put("_links", new ArrayList<>(arrayLinks));
                LOGGER.info("Sa returnat o lista vida");
                return new ResponseEntity<>(links, HttpStatus.OK);
            }
            List<EntityModel> list = new ArrayList<>();
            for (PhysiciansDTO p : physicianDTOList) {
                list.add(new PhysiciansHateoas().toModel(new Pair<>(authorizationHeader,p)));
            }
            LOGGER.info("Sa returnat o lista cu date");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (InvalidPageNumber invalidPageNumber) {
            LOGGER.info(invalidPageNumber.getMessage());
            return new ResponseEntity<>(new ErrorDto(invalidPageNumber.getMessage()), HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        }
    }

    @GetMapping("/short")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Physicians found!", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PhysiciansDTO.class)))
            }),
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
    public ResponseEntity<?> getShortDoctor(@RequestHeader("Authorization") String authorizationHeader) {
        LOGGER.info("Sau solicitat toti medici lista mica");
        UserAuthorizationDto userAuthorizationDto = this.authorizationService.decodeToken(authorizationHeader);
        if(userAuthorizationDto == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        else{
            if(userAuthorizationDto.getRole() != 1 && userAuthorizationDto.getRole() != 2){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        // daca e logcat ca pacient sau user poate veda datele
        List<PhysiciansDTO> physicians = physiciansService.findAll();
        if (physicians.isEmpty()) {
            // nu exista doar link spre parinte
            Map<String, ArrayList<Link>> links = new HashMap<>();
            ArrayList<Link> arrayLinks = new ArrayList<>();
            Link parentLink = linkTo(methodOn(PhysiciansController.class).getAllPhysician(null)).withRel("self");

            arrayLinks.add(parentLink);
            links.put("result", new ArrayList<>());
            links.put("_links", new ArrayList<>(arrayLinks));
            LOGGER.info("Sa returnat o lista vida de doctori.");
            return new ResponseEntity<>(links, HttpStatus.OK);
        }
        List<ShortPhysiciansDto> list = new ArrayList<>();
        for (PhysiciansDTO p : physicians) {
            ShortPhysiciansDto shortPhysiciansDto = new ShortPhysiciansDto(p.getId_user(),p.getFirst_name(),p.getLast_name(),p.getEmail(),p.getSpecialization());
            list.add(shortPhysiciansDto);
        }
        LOGGER.info("Sa returnat o lista nevida de doctori.");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
