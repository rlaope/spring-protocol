package io.springprotocol.examples.grpc;

import io.springprotocol.spring.EnableSpringClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.grpc.client")
public class GrpcExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcExampleApplication.class, args);
    }
}
