package io.springprotocol.examples.rest;

import io.springprotocol.spring.EnableSpringClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.rest.client")
public class RestExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestExampleApplication.class, args);
    }
}
