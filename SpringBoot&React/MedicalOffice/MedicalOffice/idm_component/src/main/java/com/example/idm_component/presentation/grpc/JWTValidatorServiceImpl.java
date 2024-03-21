package com.example.idm_component.presentation.grpc;

import com.example.idm_component.dto.BlacklistDto;
import com.example.idm_component.dto.UserAuthorizationDto;
import com.example.idm_component.dto.UserDto;
import com.example.idm_component.exceptions.ExpirationJWTException;
import com.example.idm_component.repository.BlacklistRedisRepository;
import com.example.idm_component.repository.UserRepository;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class JWTValidatorServiceImpl extends JWTValidatorGrpc.JWTValidatorImplBase {
    private final JWTManagement jwtManagement = new JWTManagement();
    private UserRepository userRepository = new UserRepository();
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTValidatorServiceImpl.class);

    // da foarte multe erori aiurea cand se fac cereri din frontend
    // redis.clients.jedis.exceptions.JedisConnectionException
    private BlacklistRedisRepository blacklistRedisRepository = new BlacklistRedisRepository();

    @Bean(name = "jwtPasswordEncoder")
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void validateJWT(com.example.idm_component.presentation.grpc.JWTValidateRequest request,
                            io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateResponse> responseObserver) {
        UserAuthorizationDto userAuthorizationDto = null;
        try {
            String token = request.getToken();
            LOGGER.info("Cerere cu: " + token);

            List<BlacklistDto> list = this.blacklistRedisRepository.findAll();
            for (BlacklistDto blacklistDto : list) {
                if (blacklistDto.getAuthorizationToken().equals(token.replace("Bearer ", ""))) {
                    responseObserver.onError(
                            Status.PERMISSION_DENIED
                                    .withDescription("Tokenul a fost folosit la logout.")
                                    .asRuntimeException()
                    );
                    return;
                }
            }
            userAuthorizationDto = this.jwtManagement.decodeToken(token.replace("Bearer ", ""));

            JWTValidateResponse response = JWTValidateResponse.newBuilder()
                    .setId(userAuthorizationDto.getId())
                    .setRole(userAuthorizationDto.getRole())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ExpirationJWTException | SignatureException e) {
            responseObserver.onError(
                    Status.PERMISSION_DENIED
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (redis.clients.jedis.exceptions.JedisConnectionException ex) {
            try {  // apar foarte multe exceptii de la redis chiar daca containerul e pornit
                String token = request.getToken();
                userAuthorizationDto = this.jwtManagement.decodeToken(token.replace("Bearer ", ""));
                JWTValidateResponse response = JWTValidateResponse.newBuilder()
                        .setId(userAuthorizationDto.getId())
                        .setRole(userAuthorizationDto.getRole())
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (ExpirationJWTException | SignatureException e) {
                responseObserver.onError(
                        Status.PERMISSION_DENIED
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );
            } catch (Exception e) {
                e.printStackTrace();
                responseObserver.onError(
                        Status.ABORTED
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(
                    Status.ABORTED
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    public void logout(com.example.idm_component.presentation.grpc.JWTValidateRequest request,
                       io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.EmptyGRPCResponse> responseObserver) {
        // se face logout oricum fie că e valid sau nu tokenul
        try {
            this.blacklistRedisRepository.save(new BlacklistDto(LocalDateTime.now(), request.getToken().replace("Bearer ", "")));
        }catch (redis.clients.jedis.exceptions.JedisConnectionException ex){
            LOGGER.info("Nu a reusit scrierea in blacklist");
        }
            EmptyGRPCResponse response = EmptyGRPCResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }

    public void login(com.example.idm_component.presentation.grpc.CredentialsRequest request,
                      io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateRequest> responseObserver) {
        LOGGER.info("Sa cerut logarea cu datele: " + request);
        if (!Pattern.matches("^[a-zA-Z0-9\\\\.-]+$", request.getUsername())) {
            LOGGER.info("Campul username poate contine doar caractere a-z A-Z 0-9 . si - !");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Campul username poate contine doar caracrere a-z A-Z 0-9 . si - !")
                            .asRuntimeException()
            );
            return;
        }
        if (!Pattern.matches("^[a-zA-Z0-9\\\\.-]+$", request.getPassword())) {
            LOGGER.info("Campul parola poate contine doar caracrere a-z A-Z 0-9 . si - !");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Campul parola poate contine doar caractere a-z A-Z 0-9 . si - !")
                            .asRuntimeException()
            );
            return;
        }

        UserDto userDto = this.userRepository.getUserByUsername(request.getUsername());
        if (userDto != null) {
            if (this.passwordEncoder().matches(request.getPassword(), userDto.getPassword())) {
                LOGGER.info("Utilizatorul cu " + request + " a fost logat cu succes.");
                String token = "Bearer " + this.jwtManagement.createJWT(userDto.getId(), userDto.getId_role());
                JWTValidateRequest response = JWTValidateRequest.newBuilder()
                        .setToken(token)
                        .setId(userDto.getId().toString())
                        .setRole(userDto.getId_role().toString())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }else{
                LOGGER.info("Utilizatorul cu " + request + " nu a putut fi logat.");
                responseObserver.onError(
                        Status.PERMISSION_DENIED
                                .withDescription("Datele de autentificare nu sunt valide.")
                                .asRuntimeException()
                );
            }
        } else {
            LOGGER.info("Utilizatorul cu " + request + " nu a putut fi logat.");
            responseObserver.onError(
                    Status.PERMISSION_DENIED
                            .withDescription("Datele de autentificare nu sunt valide.")
                            .asRuntimeException()
            );
        }
    }
}
