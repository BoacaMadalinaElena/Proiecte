package com.example.idm_component;

import com.example.idm_component.presentation.grpc.*;
import io.grpc.*;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Scanner;


@Component
public class IdmComponentApplication {
    @PostConstruct
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        UserServiceGrpc.UserServiceBlockingStub stubUser = UserServiceGrpc.newBlockingStub(channel);
        JWTValidatorGrpc.JWTValidatorBlockingStub stubJWT = JWTValidatorGrpc.newBlockingStub(channel);

        try {
            while (true) {
                System.out.println("Operatiile disponibile: \n1.Create user\n2.Login\n3.Logout\n4.Decode JWT\n");
                System.out.print("Aleg operatia cu numarul: ");
                int op = scanner.nextInt();
                String username = null;
                String password = null;
                String token = null;
                Long id = null;
                Long id_role = null;
                switch (op) {
                    case 2:
                        scanner.nextLine();
                        System.out.print("Username: ");
                        username = scanner.nextLine();
                        System.out.print("Password: ");
                        password = scanner.nextLine();

                        try {
                            JWTValidateRequest result = stubJWT.login(CredentialsRequest.newBuilder().setPassword(password).setUsername(username).build());
                            System.out.println("Raspunsul " + result);
                        } catch (StatusRuntimeException e) {
                            io.grpc.Status status = io.grpc.Status.fromThrowable(e);
                            if (status.getCode() == io.grpc.Status.Code.UNAVAILABLE) {
                                System.err.println("Server indisponibil: " + status.getDescription());
                            } else {
                                System.err.println("O eroare gRPC a aparut: " + status);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        //    private Long id;
                        //    private Long id_role;
                        //    private String username;
                        //    private String password;
                        scanner.nextLine();
                        System.out.print("id_role: ");
                        id_role = scanner.nextLong();
                        scanner.nextLine();
                        System.out.print("Username: ");
                        username = scanner.nextLine();
                        System.out.print("Password: ");
                        password = scanner.nextLine();

                        try {
                            UserDtoGRPC result = stubUser.createUser(UserDtoGRPC.newBuilder().setPassword(password).setUsername(username).setIdRole(id_role).build());
                            System.out.println("Raspunsul " + result);
                        } catch (StatusRuntimeException e) {
                            io.grpc.Status status = io.grpc.Status.fromThrowable(e);
                            if (status.getCode() == io.grpc.Status.Code.UNAVAILABLE) {
                                System.err.println("Server indisponibil: " + status.getDescription());
                            } else {
                                System.err.println("O eroare gRPC a aparut: " + status);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        //rpc logout(JWTValidateRequest) returns (EmptyGRPCResponse);
                        System.out.print("Token: ");
                        scanner.nextLine();
                        token = scanner.nextLine();
                        try {
                            EmptyGRPCResponse result = stubJWT.logout(JWTValidateRequest.newBuilder().setToken(token).build());
                            System.out.println("Raspunsul " + result);
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
                        break;
                    case 4:
                        //rpc logout(JWTValidateRequest) returns (EmptyGRPCResponse);
                        System.out.print("Token: ");
                        scanner.nextLine();
                        token = scanner.nextLine();
                        try {
                            //rpc validateJWT(JWTValidateRequest) returns (JWTValidateResponse);
                            JWTValidateResponse result = stubJWT.validateJWT(JWTValidateRequest.newBuilder().setToken(token).build());
                            System.out.println("Raspunsul " + result.getId() + " " + result.getRole());
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
                        break;
                    default:
                        System.out.println("Operatie invalida!");
                }
            }
        } catch (Exception e) {
            channel.shutdown();
        }
    }

}
