package io.springprotocol.examples.grpc.service;

import io.springprotocol.examples.grpc.client.GreeterClient;
import io.springprotocol.examples.grpc.proto.HelloReply;
import io.springprotocol.examples.grpc.proto.HelloRequest;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private final GreeterClient greeterClient;

    public GreetingService(GreeterClient greeterClient) {
        this.greeterClient = greeterClient;
    }

    public String greet(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        HelloReply reply = greeterClient.sayHello(request);
        return reply.getMessage();
    }

    public String greetAgain(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        HelloReply reply = greeterClient.greetAgain(request);
        return reply.getMessage();
    }
}
