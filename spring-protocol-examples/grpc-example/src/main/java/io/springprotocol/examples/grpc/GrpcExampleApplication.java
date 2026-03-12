package io.springprotocol.examples.grpc;

import io.springprotocol.grpc.spring.EnableGrpcClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGrpcClients(basePackages = "io.springprotocol.examples.grpc.client")
public class GrpcExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcExampleApplication.class, args);
    }
}
