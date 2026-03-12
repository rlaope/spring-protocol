package io.springprotocol.examples.graphql;

import io.springprotocol.spring.EnableSpringClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.graphql.client")
public class GraphqlExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlExampleApplication.class, args);
    }
}
