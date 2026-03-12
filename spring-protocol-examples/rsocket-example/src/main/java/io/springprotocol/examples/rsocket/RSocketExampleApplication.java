package io.springprotocol.examples.rsocket;

import io.springprotocol.spring.EnableSpringClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.rsocket.client")
public class RSocketExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RSocketExampleApplication.class, args);
    }
}
