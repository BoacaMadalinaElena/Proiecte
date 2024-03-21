package com.example.idm_component.service;

import com.example.idm_component.exception.UnauthorizedException;
import com.example.idm_component.presentation.grpc.EmptyGRPCResponse;
import com.example.idm_component.presentation.grpc.JWTValidateRequest;
import com.example.idm_component.dto.AuthorizedInfo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class IDMService {
    private com.example.idm_component.presentation.grpc.JWTValidatorGrpc.JWTValidatorBlockingStub stubJWT;
    public  IDMService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        this.stubJWT = com.example.idm_component.presentation.grpc.JWTValidatorGrpc.newBlockingStub(channel);
    }

    public AuthorizedInfo login(String username, String password) throws UnauthorizedException {
        com.example.idm_component.presentation.grpc.JWTValidateRequest result;
        try {
            result = this.stubJWT.login(com.example.idm_component.presentation.grpc.CredentialsRequest.newBuilder().setPassword(password).setUsername(username).build());
            AuthorizedInfo authorizedInfo = new AuthorizedInfo();
            System.out.println(result.getToken());
            authorizedInfo.setId(result.getId());
            authorizedInfo.setRole(result.getRole());
            authorizedInfo.setToken(result.getToken());
            return authorizedInfo;
        }catch ( Exception ex){
            io.grpc.Status status = io.grpc.Status.fromThrowable(ex);
            if(status.getCode() == Status.Code.PERMISSION_DENIED){
                throw new UnauthorizedException(status.getDescription());
            }
            if(status.getCode() == Status.Code.INVALID_ARGUMENT){
                throw new UnauthorizedException(status.getDescription());
            }
            System.out.println(ex.getMessage());
            throw new RuntimeException(new RuntimeException("Eroare grpc consultati log-urile"));    // daca e altceva decat credentiale invalide
        }
    }

    public boolean logout(String token){
        try {
            EmptyGRPCResponse result = stubJWT.logout(JWTValidateRequest.newBuilder().setToken(token).build());
            System.out.println("Raspunsul " + result);
            return  true;
        } catch (StatusRuntimeException e) {
            io.grpc.Status status = io.grpc.Status.fromThrowable(e);
            if (status.getCode() == io.grpc.Status.Code.UNAVAILABLE) {
                System.err.println("Server indisponibil: " + status.getDescription());
            } else {
                System.err.println("O eroare gRPC a aparut: " + status);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return  false;
    }

}
