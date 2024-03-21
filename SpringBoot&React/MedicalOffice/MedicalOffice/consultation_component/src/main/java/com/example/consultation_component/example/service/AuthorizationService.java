package com.example.consultation_component.example.service;

import com.example.idm_component.presentation.grpc.JWTValidateRequest;
import com.example.idm_component.presentation.grpc.JWTValidateResponse;
import com.example.idm_component.presentation.grpc.JWTValidatorGrpc;
import com.example.consultation_component.example.dto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    public UserAuthorizationDto decodeToken(String token){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        JWTValidatorGrpc.JWTValidatorBlockingStub stubJWT = JWTValidatorGrpc.newBlockingStub(channel);
        try {
            //rpc validateJWT(JWTValidateRequest) returns (JWTValidateResponse);
            JWTValidateResponse result = stubJWT.validateJWT(JWTValidateRequest.newBuilder().setToken(token).build());
            System.out.println("Raspunsul " + result.getId() + " " + result.getRole());
            return new UserAuthorizationDto(result.getId(),result.getRole());
        } catch (StatusRuntimeException e) {
            io.grpc.Status status = io.grpc.Status.fromThrowable(e);
            if (status.getCode() == io.grpc.Status.Code.UNAVAILABLE) {
                System.err.println("Server indisponibil: " + status.getDescription());
                return  null;
            } else {
                System.err.println("O eroare gRPC a aparut: " + status);
                return  null;
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return  null;
        }
    }
}
