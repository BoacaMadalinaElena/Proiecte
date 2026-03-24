package org.example.controller;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.dto.ErrorDto;
import org.example.dto.IpPublicAddressDto;
import org.example.exception.ConflictException;
import org.example.exception.InternalServerError;
import org.example.exception.InvalidFieldException;
import org.example.other.CustomPrinter;
import org.example.service.IPAddressManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/cluster/ipAddress")
public class IPAddressManagementController {
    @Autowired
    private IPAddressManagementService ipAddressManagementService;
    @Value("${internPassword}")
    private String internPassword;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ip address inserted!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = IpPublicAddressDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict exception!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid field!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            )
    })
    @PostMapping()
    public ResponseEntity<?> addIp(@RequestHeader("Authorization") String authorizationHeader,@RequestBody IpPublicAddressDto ipPublicAddressDto) {
        if(!authorizationHeader.equals(this.internPassword)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printInfo("Cerere de adaugare a adresei: " + ipPublicAddressDto);
        try {
            IpPublicAddressDto ipPublicAddressDto1 =this.ipAddressManagementService.add(ipPublicAddressDto.getIp(), ipPublicAddressDto.getPort());
            CustomPrinter.printSuccess("Adresa ip: " + ipPublicAddressDto + " a fost adaugata cu succes!");
            return new ResponseEntity<>(ipPublicAddressDto1, HttpStatus.OK);
        } catch (ConflictException conflictException) {
            CustomPrinter.printErr("A aparut o eroare la adaugarea adresei: " + ipPublicAddressDto + " eroarea este: " + conflictException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(conflictException.getErrorRo(), conflictException.getErrorEng()), HttpStatus.CONFLICT);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("A aparut o eroare la adaugarea adresei: " + ipPublicAddressDto + " eroarea este: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ip address deleted!"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid field!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            )
    })
    @DeleteMapping()
    public ResponseEntity<?> deleteIp(@RequestHeader("Authorization") String authorizationHeader,@RequestBody IpPublicAddressDto ipPublicAddressDto) {
        if(!authorizationHeader.equals(this.internPassword)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printInfo("Cerere de stergere a adresei ip: " + ipPublicAddressDto);
        try {
            Optional<IpPublicAddressDto> inMemory = this.ipAddressManagementService.findByIpAndPort(ipPublicAddressDto.getIp(),ipPublicAddressDto.getPort());
            if(inMemory.isEmpty()){
                CustomPrinter.printInfo("Cererea de stergere a adresei ip: " + ipPublicAddressDto + " a esuat, adresa nu exista!");
                return new ResponseEntity<>( HttpStatus.NOT_FOUND);
            }
            this.ipAddressManagementService.deleteIpAddress(ipPublicAddressDto.getIp(), ipPublicAddressDto.getPort());
            CustomPrinter.printSuccess("Stergerea adresei ip: " + ipPublicAddressDto + " a fost facuta cu succes!");
            return new ResponseEntity<>( inMemory,HttpStatus.NO_CONTENT);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("A aparut o eroare la stergerea adresei: " + ipPublicAddressDto + " eroarea este: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/ipAndPort/{ip}/{port}")
    public ResponseEntity<?> getIpAndPort(@RequestHeader("Authorization") String authorizationHeader,@PathVariable String  ip,@PathVariable int port) {
        if(!authorizationHeader.equals(this.internPassword)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printInfo("Cerere de verificare a existentei  adresei ip: " + ip + ":" + port);
        try {
            Optional<IpPublicAddressDto> inMemory = this.ipAddressManagementService.findByIpAndPort(ip,port);
            if(inMemory.isEmpty()){
                CustomPrinter.printInfo("Cererea de extragere a adresei ip: " + ip + ":" + port + " a esuat, adresa nu exista!");
                return new ResponseEntity<>( HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>( inMemory,HttpStatus.OK);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("A aparut o eroare la gasirea adresei: " +  ip + ":" + port  + " eroarea este: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ip address!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = IpPublicAddressDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            )
    })
    @GetMapping
    public ResponseEntity<?> getIpAddress(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole) {
        CustomPrinter.printInfo("Cerere noua de obtinere a unei adrese ip de la: " + userId);
        try {
            CustomPrinter.printInfo("Cerere noua de obtinere a unei adrese ip de la utilizatorul cu id-ul:" +userId);
            IpPublicAddressDto ipPublicAddressDto = this.ipAddressManagementService.getIpPublicAddress();
            CustomPrinter.printSuccess("A fost returnat un id de conectare pentru utilizatorul cu id-ul: " + userId);
            return new ResponseEntity<>(ipPublicAddressDto, HttpStatus.OK);
        } catch (InternalServerError ipAddress) {
            CustomPrinter.printErr("A aparut o eroare interna: " + ipAddress.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(ipAddress.getErrorRo(), ipAddress.getErrorEng()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
