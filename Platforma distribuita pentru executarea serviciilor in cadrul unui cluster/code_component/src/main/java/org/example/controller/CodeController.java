package org.example.controller;


import com.google.gson.Gson;
import org.example.dto.CodeRecordRequest;
import org.example.dto.ErrorDto;
import org.example.dto.ResponseGetCode;
import org.example.exception.InvalidFieldException;
import org.example.other.CustomPrinter;
import org.example.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

@RestController
@RequestMapping("/api/cluster/code")
public class CodeController {
    @Autowired
    private CodeService codeService;
    private final Gson gson = new Gson();
    @Value("${internPassword}")
    private String internPassword;

    @PostMapping
    public ResponseEntity<?> insertCode(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @RequestBody CodeRecordRequest codeRecordRequest) {
        try {
            CustomPrinter.printInfo("Cerere noua de la utilizatorul: " + userId);
            codeRecordRequest.setUserId(userId);
            this.codeService.insert(codeRecordRequest);
            CustomPrinter.printSuccess("Insert code successfully for user: " + userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de inserare cod a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    // client
    @GetMapping
    public ResponseEntity<?> getCode(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @RequestParam(required = false, defaultValue = "0") Integer page,
                                     @RequestParam(required = false, defaultValue = "6") Integer size,
                                     @RequestParam(required = false, defaultValue = "") String title) {
        CustomPrinter.printInfo("Cerere noua de la utilizatorul: " + userId);
        try {
            CustomPrinter.printInfo("New get code request for user: " + userId);
            ResponseGetCode responseGetCode = this.codeService.getAllCodes(page, size, title);
            CustomPrinter.printSuccess("The code request of the " +userId + " user was made successfully.");
            return new ResponseEntity<>(responseGetCode, HttpStatus.OK);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de cautare cod a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCodeById(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @PathVariable String id) {
        CustomPrinter.printInfo("Cerere noua de la utilizatorul: " + userId);
        try {
            CustomPrinter.printInfo("Request for code with id " + id + " from user" + userId);
            CodeRecordRequest codeRecordRequest = this.codeService.getById(id);
            if (codeRecordRequest != null) {
                String str = this.gson.toJson(codeRecordRequest, CodeRecordRequest.class);
                CustomPrinter.printSuccess("The request for the code with id " + id + " from the user " + userId + " was made successfully.");
                return new ResponseEntity<>(str, HttpStatus.OK);
            } else {
                CustomPrinter.printSuccess("The request for the code with id " + id + " from the user " + userId + " was made error, not found.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de cautare cod a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    // intern
    @GetMapping("/intern/{id}")
    public ResponseEntity<?> getCodeByIdIntern(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        try {
            if (!authorizationHeader.equals(this.internPassword)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            CustomPrinter.printInfo("Cerere noua interna pentru codul cu id-ul: " + id);
            CodeRecordRequest codeRecordRequest = this.codeService.getByIdIntern(id);
            if (codeRecordRequest != null) {
                String str = this.gson.toJson(codeRecordRequest, CodeRecordRequest.class);
                CustomPrinter.printSuccess("Cererea interna pentru codul cu id-ul: " + id + " a fost facuta cu succes!");
                return new ResponseEntity<>(str, HttpStatus.OK);
            } else {
                CustomPrinter.printErr("Cererea interna pentru codul cu id-ul: " + id + " a generat eroare, inregistrarea nu exista!");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de cautare cod a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    // client
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @PathVariable String id) {
        CustomPrinter.printInfo("Cerere noua de la utilizatorul: " + userId);
        try {
            CustomPrinter.printInfo("Cerere noua de stergere a codului cu id-ul: " + id + " de la utilizatorul: " + userId);
            CodeRecordRequest codeRecordRequest = this.codeService.getById(id);
            if (codeRecordRequest != null) {
                if(!Objects.equals(codeRecordRequest.getUserId(), userId) && !Objects.equals(userRole, "admin")){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                this.codeService.deleteById(id);
                CustomPrinter.printSuccess("Cerere noua de stergere a codului cu id-ul: " + id + " de la utilizatorul: " + userId + " a fost facuta cu succes.");
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                CustomPrinter.printSuccess("Cerere noua de stergere a codului cu id-ul: " + id + " de la utilizatorul: " + userId + " a generat eroare, nu exista codul.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de stergere de cod a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
