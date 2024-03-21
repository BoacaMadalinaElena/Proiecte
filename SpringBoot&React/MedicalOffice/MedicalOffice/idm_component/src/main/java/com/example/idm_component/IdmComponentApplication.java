package com.example.idm_component;

import com.example.idm_component.presentation.grpc.JWTValidatorServiceImpl;
import com.example.idm_component.presentation.grpc.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.annotation.PostConstruct;


public class IdmComponentApplication {
    @PostConstruct
    public static void main(String[] args) {
        System.out.println("Serverul gRPC asculta pe portul 8080!");

        try {
            Server server = ServerBuilder
                    .forPort(8080)
                    .addService(new JWTValidatorServiceImpl())
                    .addService(new UserServiceImpl())
                    .build();

            server.start();

            // Create a new thread for gRPC
            Thread grpcThread = new Thread(() -> {
                try {
                    server.awaitTermination();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            grpcThread.start();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}