package com.example.httpnode.controller;

import com.example.httpnode.dto.*;
import com.example.httpnode.dto.enums.RoleType;
import com.example.httpnode.exception.ConflictException;
import com.example.httpnode.exception.InvalidFieldException;
import com.example.httpnode.exception.UnauthorizedException;
import com.example.httpnode.other.CustomPrinter;
import com.example.httpnode.service.IDMService;
import com.example.httpnode.service.IPAddressManagementService;
import com.example.httpnode.service.SecurityService;
import com.example.httpnode.service.TokenService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

//http://localhost:8090/swagger-ui/index.html#/

@RestController
@RequestMapping("/api/cluster/user")
@Component
public class IDMController {
    @Autowired
    private IDMService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private IPAddressManagementService ipAddressManagementService;
    @Autowired
    private SecurityService securityService;
    private static final Map<String, IpAndTimeDto> synchronizedHashMap = Collections.synchronizedMap(new HashMap<>());

    @Value("${internPassword}")
    private String internPassword;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ShortUser.class))
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
                    responseCode = "409",
                    description = "Conflict!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            )
    })
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@RequestBody UserDto userDto) {
        try {
            CustomPrinter.printInfo("Cerere noua de creare cont cu datele: " + userDto);
            userDto.setRole(RoleType.user);
            userService.saveUser(userDto);
            CustomPrinter.printSuccess("Contul cu datele: " + userDto + " a fost creat cu succes!");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("A aparut o eroare la crearea contului: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ConflictException conflictException) {
            CustomPrinter.printErr("A aparut o eroare la crearea contului: " + conflictException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(conflictException.getErrorRo(), conflictException.getErrorEng()), HttpStatus.CONFLICT);
        }
    }


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ShortUser.class))
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
                    responseCode = "401",
                    description = "Invalid code!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))
                    }
            )
    })
    @PostMapping("/validateAccount")
    public ResponseEntity<?> validateAccount(@RequestBody UserCode userDto) {
        try {
            CustomPrinter.printInfo("Cerere noua de confirmare cu  codul: " + userDto);
            UserDto user = userService.saveUserToDatabase(userDto);
            user.setPassword(null);
            return new ResponseEntity<>(new ShortUser(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getDescription()), HttpStatus.CREATED);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("A aparut o eroare la crearea contului: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ConflictException conflictException) {
            CustomPrinter.printErr("A aparut o eroare la crearea contului: " + conflictException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(conflictException.getErrorRo(), conflictException.getErrorEng()), HttpStatus.CONFLICT);
        } catch (UnauthorizedException conflictException) {
            CustomPrinter.printErr("A aparut o eroare la crearea contului: " + conflictException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(conflictException.getErrorRo(), conflictException.getErrorEng()), HttpStatus.CONFLICT);

        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ShortUser.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found!"
            )
    })
    @GetMapping("/getUserById/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            ShortUser shortUser = this.userService.findUserById(id);
            if (shortUser == null) {
                CustomPrinter.printErr("Cererea pentru utilizatorul: " + id + " a generat eroare, resursa nu exista!");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                CustomPrinter.printErr("Cererea pentru utilizatorul: " + id + " a fost facuta cu succes");
                return new ResponseEntity<>(shortUser, HttpStatus.OK);
            }
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de cautare a unitilatorului a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User logged!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityDto.class))
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
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            CustomPrinter.printInfo("Cerere noua de login cu datele: " + loginDto);
            UserIdentityDto userIdentityDto = this.userService.login(loginDto);
            CustomPrinter.printSuccess("Cererea de login cu datele: " + loginDto + " a fost facuta cu succes!");
            return new ResponseEntity<>(userIdentityDto, HttpStatus.OK);
        } catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de login cu datele: " + loginDto + " a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UnauthorizedException unauthorizedException) {
            CustomPrinter.printErr("Cererea de login cu datele: " + loginDto + " a generat eroarea: " + unauthorizedException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        } catch (Exception exception) {
            CustomPrinter.printErr("Cererea de login cu datele: " + loginDto + " a generat eroarea: " + exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User logged!",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityDto.class))

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

    @PostMapping("/validateJWS")
    public ResponseEntity<?> validateJWS(@RequestHeader("Authorization") String authorizationHeader, @RequestBody TokenDto tokenDto) {
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printInfo("Cerere de validare pentru jwt-ul: " + tokenDto.getToken());
        try {
            UserAuthorizationDto userAuthorizationDto = this.tokenService.decodeToken(tokenDto.getToken());
            if(this.securityService.validate(userAuthorizationDto.getId()) != null){
                return new ResponseEntity<>(new ErrorDto("Acest cont a fost blocat până la: " + this.securityService.validate(userAuthorizationDto.getId()) + " GTM","This account has been blocked until: " + this.securityService.validate(userAuthorizationDto.getId() + " GTM")),HttpStatus.FORBIDDEN);
            }
            boolean ip = this.ipAddressManagementService.findByIpAndPort(tokenDto.getIp().split(":")[0], Integer.parseInt(tokenDto.getIp().split(":")[1]));

                CustomPrinter.printNormal(tokenDto.toString() + " exista: " + ip);
                CustomPrinter.printNormal("Nodurile conectate acum: ");
                for(Map.Entry<String, IpAndTimeDto> ipAndTimeDto : synchronizedHashMap.entrySet()){
                    CustomPrinter.printNormal(ipAndTimeDto.getKey() + " " + ipAndTimeDto.getValue());
                }
                try {
                    CustomPrinter.printInfo("userAuthorizationDto.getId()).getTime(): " + synchronizedHashMap.get(userAuthorizationDto.getId()).getTime() + " now: " + LocalDateTime.now());
                }catch (Exception ex){

                }
            if (synchronizedHashMap.containsKey(userAuthorizationDto.getId()) && !synchronizedHashMap.get(userAuthorizationDto.getId()).getTime().plusMinutes(5).isBefore(LocalDateTime.now())   && ip) {
                CustomPrinter.printErr(  new ErrorDto(
                        "Nu poate exista decât o conexiune de execuție deschisă de un utilizator la un moment dat. Vă rugăm să executați codul doar după ce ați terminat de executat codul anterior.",
                        "Only one execution connection can be open per user at a time. Please execute code only after you have finished executing the previous code."
                ).toString());
                return new ResponseEntity<>(
                        new ErrorDto(
                                "Nu poate exista decât o conexiune de execuție deschisă de un utilizator la un moment dat. Vă rugăm să executați codul doar după ce ați terminat de executat codul anterior.",
                                "Only one execution connection can be open per user at a time. Please execute code only after you have finished executing the previous code."
                        ),
                        HttpStatus.SERVICE_UNAVAILABLE);
            } else {
                synchronizedHashMap.put(userAuthorizationDto.getId(), new IpAndTimeDto( tokenDto.getIp(), LocalDateTime.now() ));
            }
            CustomPrinter.printSuccess("JWT-ul: " + tokenDto.getToken() + " a fost validat cu succes!");
            return new ResponseEntity<>(userAuthorizationDto, HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            CustomPrinter.printErr("A aparut o eroare la validarea jwt-ului: " + tokenDto.getToken() + " eroarea este: " + unauthorizedException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        }  catch (java.lang.NullPointerException exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(new ErrorDto("Camp invalid!", "Invalid field!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping("/validateJWSMessage")
    public ResponseEntity<?> validateJWSMessage(@RequestHeader("Authorization") String authorizationHeader, @RequestBody TokenDto tokenDto) {
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printInfo("Cerere de validare pentru jwt-ul: " + tokenDto.getToken());
        try {
            UserAuthorizationDto userAuthorizationDto = this.tokenService.decodeToken(tokenDto.getToken());
            if(this.securityService.validate(userAuthorizationDto.getId()) != null){
                return new ResponseEntity<>(new ErrorDto("Acest cont a fost blocat până la: " + this.securityService.validate(userAuthorizationDto.getId()) + " GTM","This account has been blocked until: " + this.securityService.validate(userAuthorizationDto.getId() + " GTM")),HttpStatus.FORBIDDEN);
            }
            CustomPrinter.printSuccess("JWT-ul: " + tokenDto.getToken() + " a fost validat cu succes!");
            return new ResponseEntity<>(userAuthorizationDto, HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            CustomPrinter.printErr("A aparut o eroare la validarea jwt-ului: " + tokenDto.getToken() + " eroarea este: " + unauthorizedException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        }  catch (java.lang.NullPointerException exception) {
          CustomPrinter.printErr(exception.getMessage());
            return new ResponseEntity<>(new ErrorDto("Camp invalid!", "Invalid field!"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping("/validateJWSLogs")
    public ResponseEntity<?> validateJWSLogs(@RequestHeader("Authorization") String authorizationHeader, @RequestBody TokenDto tokenDto) {
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printInfo("Cerere de validare pentru jwt-ul: " + tokenDto.getToken());
        try {
            CustomPrinter.printWarning(tokenDto.getToken());
            UserAuthorizationDto userAuthorizationDto = this.tokenService.decodeToken(tokenDto.getToken().replace("Bearer ", ""));
            if(this.securityService.validate(userAuthorizationDto.getId()) != null){
                return new ResponseEntity<>(new ErrorDto("Acest cont a fost blocat până la: " + this.securityService.validate(userAuthorizationDto.getId()) + " GTM","This account has been blocked until: " + this.securityService.validate(userAuthorizationDto.getId() + " GTM")),HttpStatus.FORBIDDEN);
            }
            CustomPrinter.printSuccess("JWT-ul: " + tokenDto.getToken() + " a fost validat cu succes!");
            return new ResponseEntity<>(userAuthorizationDto, HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            CustomPrinter.printErr("A aparut o eroare la validarea jwt-ului: " + tokenDto.getToken() + " eroarea este: " + unauthorizedException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logoutJWS")
    public ResponseEntity<?> logoutJWS(@RequestHeader("Authorization") String authorizationHeader, @RequestBody TokenDto tokenDto) {
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomPrinter.printNormal("Cerere de logout intern pentru jwt-ul: " + tokenDto.getToken());
        try {
            UserAuthorizationDto userAuthorizationDto = this.tokenService.decodeToken(tokenDto.getToken());
            CustomPrinter.printSuccess("JWT-ul: " + tokenDto.getToken() + " a fost validat cu succes!");
            synchronizedHashMap.remove(userAuthorizationDto.getId());
            CustomPrinter.printNormal("Remove token, size: " + synchronizedHashMap);
            return new ResponseEntity<>(userAuthorizationDto, HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            CustomPrinter.printErr("A aparut o eroare la validarea jwt-ului: " + tokenDto.getToken() + " eroarea este: " + unauthorizedException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/securityAttack")
    public ResponseEntity<?> securityAttack(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SecurityAttack securityAttack) {
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        this.securityService.add(securityAttack.getUserId());
        CustomPrinter.printInfo("Atac de securitate de la: " + securityAttack.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logoutJWSExtern")
    public ResponseEntity<?> logoutJWSExtern(@RequestHeader("Authorization") String authorizationHeader) {
        CustomPrinter.printInfo("Cerere de logout extern pentru jwt-ul: " + authorizationHeader);
        try {
            UserAuthorizationDto userAuthorizationDto = this.tokenService.decodeToken(authorizationHeader.replace("Bearer ", ""));
            CustomPrinter.printSuccess("JWT-ul: " + authorizationHeader + " a fost validat cu succes!");
            synchronizedHashMap.remove(userAuthorizationDto.getId());
            this.tokenService.logout(authorizationHeader);
            return new ResponseEntity<>(userAuthorizationDto, HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            CustomPrinter.printErr("A aparut o eroare la validarea jwt-ului: " + authorizationHeader + " eroarea este: " + unauthorizedException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/change-password-send-code")
    public ResponseEntity<?> changePasswordSendCode(@RequestBody UsernameOrEmail usernameOrEmail) {
        try {
            Optional<UserDto> userDto = this.userService.findByEmail(usernameOrEmail.getField());
            if(userDto.isEmpty()){
                return new ResponseEntity<>(new ErrorDto("Nu există nici un cont cu acest email!", "There is no account with this email!"), HttpStatus.UNAUTHORIZED);
            }
            if(userDto.get().getRole() == RoleType.admin){
                return new ResponseEntity<>(new ErrorDto("Parola administratorului nu se poate schimba!","The administrator's password cannot be changed!" ), HttpStatus.UNAUTHORIZED);
            }
            CustomPrinter.printInfo("Cerere de resetare parola cu: " + usernameOrEmail);
            this.userService.sendCoeResetPassword(usernameOrEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/change-password-send-code-id")
    public ResponseEntity<?> changePasswordSendCodeId(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @RequestBody IdDto idDto) {
        try {
            if(userRole.equals("admin")){
                return new ResponseEntity<>(new ErrorDto("Parola administratorului nu se poate schimba!","The administrator's password cannot be changed!" ), HttpStatus.UNAUTHORIZED);
            }
            CustomPrinter.printInfo("Cerere de resetare parola cu: " + idDto.getField());
            UserDto userDto = this.userService.findUserByIdFull(idDto.getField());
            if (userDto != null) {
                this.userService.sendCoeResetPassword(new UsernameOrEmail(userDto.getEmail(), idDto.getLanguage()));
                return new ResponseEntity<>(new UsernameOrEmail(userDto.getEmail(), idDto.getLanguage()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ErrorDto("Nu există utilizatorul!", "User not found!"), HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedException unauthorizedException) {
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        } catch (InvalidFieldException e) {
            return new ResponseEntity<>(new ErrorDto(e.getErrorRo(), e.getErrorEng()), HttpStatus.UNAUTHORIZED);

        }
    }

    @PostMapping("/change-password-check-code")
    public ResponseEntity<?> changePasswordCheckCode(@RequestBody UserCode userCode) {
        try {
            CustomPrinter.printInfo("Cerere de resetare parola cu informatiile: " + userCode);
            String token = this.userService.userCheckCode(userCode);
            CustomPrinter.printInfo("Sa generat token de resetare a parolei cu succes!" + token);
            return new ResponseEntity<>(new TokenDto(token, null), HttpStatus.OK);
        } catch (UnauthorizedException unauthorizedException) {
            return new ResponseEntity<>(new ErrorDto(unauthorizedException.getErrorRo(), unauthorizedException.getErrorEng()), HttpStatus.UNAUTHORIZED);
        } catch (NoSuchAlgorithmException ignored) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> changePassword(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @RequestBody NewPasswordDto newPasswordDto) {
        try {
            if(userRole.equals("admin")){
                return new ResponseEntity<>(new ErrorDto("Parola administratorului nu se poate schimba!","The administrator's password cannot be changed!" ), HttpStatus.UNAUTHORIZED);
            }
            CustomPrinter.printInfo("Cerere de schimbare a parolei pentru utilizatorul: " + userId);
            UserIdentityDto userIdentityDto = this.userService.changePassword(userId, newPasswordDto.getPassword());
            CustomPrinter.printInfo("Cerere de schimbare a parolei pentru utilizatorul: " + userId + " a fost facuta cu succes!");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException ex) {
            CustomPrinter.printErr("UnauthorizedException with token: " + userId);
            return new ResponseEntity<>(new ErrorDto(ex.getErrorRo(), ex.getErrorEng()), HttpStatus.UNAUTHORIZED);
        } catch (InvalidFieldException invalidFieldException) {
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @RequestBody ShortUser shortUser) {
        try {
            CustomPrinter.printInfo("Cerere de schimbare  pentru utilizatorul: " + userId);
            this.userService.updateUser(shortUser);
            CustomPrinter.printInfo("Cerere de schimbare  pentru utilizatorul: " + userId + " a fost facuta cu succes!");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFieldException invalidFieldException) {
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ConflictException e) {
            return new ResponseEntity<>(new ErrorDto(e.getErrorRo(), e.getErrorEng()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restart(@RequestHeader("Authorization") String authorizationHeader, @RequestBody String ipAndPort) {
        CustomPrinter.printInfo("Cerere de eliminare a tuturor utiliatorilor conecati la: " + ipAndPort);
        if (!authorizationHeader.equals(this.internPassword)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        for(Map.Entry<String,IpAndTimeDto> s : synchronizedHashMap.entrySet() ){
            if(Objects.equals(s.getValue().getIp(), ipAndPort)){
                CustomPrinter.printSuccess("Remove: " + s.getKey());
                synchronizedHashMap.remove(s.getKey());
            }
        }
        CustomPrinter.printSuccess("Au fost eliminati toti utilizatori conectati la: " + ipAndPort);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
