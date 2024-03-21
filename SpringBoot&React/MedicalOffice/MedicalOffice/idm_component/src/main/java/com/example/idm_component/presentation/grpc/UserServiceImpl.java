package com.example.idm_component.presentation.grpc;

import com.example.idm_component.dto.UserDto;
import com.example.idm_component.repository.UserRepository;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private UserRepository userRepository = new UserRepository();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Bean(name = "userPasswordEncoder")
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JWTManagement jwtManagement = new JWTManagement();

    public void createUser(com.example.idm_component.presentation.grpc.UserDtoGRPC request,
                           io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.UserDtoGRPC> responseObserver) {
        LOGGER.info("Sa cerut crearea: " + request);
        if (request.getUsername().length() > 100) {
            LOGGER.error("Lungimea numelui de utilizator nu poate avea msai mult de 100 de caractere");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Lungimea numelui de utilizator nu poate avea msai mult de 100 de caractere")
                            .asRuntimeException()
            );
        }
        if (request.getPassword().length() > 100) {
            LOGGER.error("Lungimea parolei nu poate avea msai mult de 100 de caractere");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Lungimea parolei nu poate avea msai mult de 100 de caractere")
                            .asRuntimeException()
            );
        }
        UserDto userDto = new UserDto(request.getId(), request.getIdRole(), request.getUsername(), request.getPassword());
        userDto.setId(null);
        if (userDto.getPassword() == null || userDto.getUsername() == null || userDto.getId_role() == null) {
            LOGGER.info("Campul username, password si id role nu poate fi null!");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Campul username, password si id role nu poate fi null!")
                            .asRuntimeException()
            );
        }
        if (!Pattern.matches("^[a-zA-Z0-9\\\\.-]+$", userDto.getUsername())) {
            LOGGER.info("Campul username poate contine doar caracrere a-z A-Z 0-9 . si - !");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Campul username poate contine doar caracrere a-z A-Z 0-9 . si - !")
                            .asRuntimeException()
            );
        }
        if (!Pattern.matches("^[a-zA-Z0-9\\\\.-]+$", userDto.getPassword())) {
            LOGGER.info("Campul parola poate contine doar caracrere a-z A-Z 0-9 . si - !");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Campul parola poate contine doar caracrere a-z A-Z 0-9 . si - !")
                            .asRuntimeException()
            );
        }
        try {
            userDto.setPassword(this.passwordEncoder().encode(userDto.getPassword()));
            UserDto savedUser = userRepository.addUser(userDto);
            LOGGER.info("Utilizatorul a fost creat");
            String token = this.jwtManagement.createJWT(savedUser.getId(), savedUser.getId_role());
            savedUser.setPassword(null);
            UserDtoGRPC userDtoGRPC = UserDtoGRPC.newBuilder()
                    .setId(savedUser.getId())
                    .setIdRole(savedUser.getId_role())
                    .setUsername(savedUser.getUsername())
                    .build();
            responseObserver.onNext(userDtoGRPC);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("user_role_FK")) {
                LOGGER.error("Nu exista nici un rol cu acest id");
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Nu exista nici un rol cu acest id")
                                .asRuntimeException()
                );
            } else if (ex.getMessage().contains("Column 'password' cannot be null")) {
                LOGGER.error("Campul parola nu poate fi null");
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Campul parola nu poate fi null")
                                .asRuntimeException()
                );
            } else if (ex.getMessage().contains("Column 'username' cannot be null")) {
                LOGGER.error("Campul username nu poate fi null");
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Campul username nu poate fi null")
                                .asRuntimeException()
                );
            } else if (ex.getMessage().contains("user_UN")) {
                LOGGER.error("Exista deja un utilizator cu acest nume de utilizator");
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Exista deja un utilizator cu acest nume de utilizator")
                                .asRuntimeException()
                );
            } else {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription(ex.getMessage())
                                .asRuntimeException()
                );
            }
            LOGGER.error("Alta eraore: " + ex.getMessage());

        }
    }
}
