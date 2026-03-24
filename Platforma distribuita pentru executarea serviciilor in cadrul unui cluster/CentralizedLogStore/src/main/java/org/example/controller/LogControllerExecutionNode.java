package org.example.controller;

import org.example.dto.*;
import org.example.exception.InvalidFieldException;
import org.example.service.HTTPClientAuthorization;
import org.example.service.LogServiceExecutionNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/cluster/logsExecutionNode")
@CrossOrigin
public class LogControllerExecutionNode {
    @Autowired
    private LogServiceExecutionNode logService;
    @Value("${internPassword}")
    private String internPassword;
    @Autowired
    private HTTPClientAuthorization httpClientAuthorization;

    @PostMapping()
    public ResponseEntity<?> insertInExecutionNodeLogs(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestBody List<LogsMessageExecutionNode> logs) {
        if(!authorizationHeader.equals(this.internPassword)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            System.out.println("New /api/cluster/logsExecutionNode post");
            this.logService.saveLogs(logs);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (InvalidFieldException invalidFieldException){
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<?> getAllByPage(@RequestHeader("Authorization") String authorizationHeader,
                                          @RequestParam(required = false,defaultValue = "0") Integer page,
                                          @RequestParam(required = false,defaultValue = "10") Integer size,
                                          @RequestParam(required = false,defaultValue = "") String time,
                                          @RequestParam(required = false,defaultValue = "") String address,
                                          @RequestParam(required = false,defaultValue = "") String type,
                                          @RequestParam(required = false,defaultValue = "") String userId) {
        UserAuthorizationDto userAuthorizationDto = this.httpClientAuthorization.validateAndDeserializeJWT(new TokenDto(authorizationHeader, null));
        if (userAuthorizationDto == null ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else if(!Objects.equals(userAuthorizationDto.getRoleType(), "admin")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
           ResponseGetLogsExecutionNode logsMessageList = this.logService.getAll(page, size,time,address,type,userId);
            return new ResponseEntity<>(logsMessageList, HttpStatus.OK);
        } catch (InvalidFieldException invalidFieldException) {
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll(@RequestHeader("Authorization") String authorizationHeader) {
        UserAuthorizationDto userAuthorizationDto = this.httpClientAuthorization.validateAndDeserializeJWT(new TokenDto(authorizationHeader, null));
        if (userAuthorizationDto == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else if(!Objects.equals(userAuthorizationDto.getRoleType(), "admin")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
           this.logService.deleteAll();
           return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
