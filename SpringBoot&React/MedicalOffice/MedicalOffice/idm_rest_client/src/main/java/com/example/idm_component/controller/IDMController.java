package com.example.idm_component.controller;

import com.example.idm_component.dto.AuthorizedInfo;
import com.example.idm_component.dto.ErrorDto;
import com.example.idm_component.dto.LoginDto;
import com.example.idm_component.dto.Pair;
import com.example.idm_component.exception.UnauthorizedException;
import com.example.idm_component.hateoas.IDMHateoas;
import com.example.idm_component.service.IDMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pos/idm")
public class IDMController {
    @Autowired
    IDMService idmService;

    private static final Logger LOGGER = LoggerFactory.getLogger(IDMController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            AuthorizedInfo result = this.idmService.login(loginDto.getUsername(), loginDto.getPassword());
            return new ResponseEntity<>(new IDMHateoas().toModel(new Pair<>("",this.idmService.login(loginDto.getUsername(), loginDto.getPassword()))), HttpStatus.OK);
        }catch (UnauthorizedException ex){
            return new ResponseEntity<>(new ErrorDto(ex.getMessage()), HttpStatus.UNAUTHORIZED);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if(this.idmService.logout(authorizationHeader)){
            return  new ResponseEntity<>(HttpStatus.OK);
        }else{
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
