package io.springprotocol.examples.grpc.service;

import org.springframework.stereotype.Service;

/**
 * Example service demonstrating gRPC client usage.
 *
 * <pre>
 * {@code
 * @Service
 * public class GreetingService {
 *
 *     private final GreeterClient greeterClient;
 *
 *     public GreetingService(GreeterClient greeterClient) {
 *         this.greeterClient = greeterClient;
 *     }
 *
 *     public String greet(String name) {
 *         HelloRequest request = HelloRequest.newBuilder()
 *                 .setName(name)
 *                 .build();
 *         HelloReply reply = greeterClient.sayHello(request);
 *         return reply.getMessage();
 *     }
 * }
 * }
 * </pre>
 */
@Service
public class GreetingService {
    // Uncomment and use once proto-generated classes are available.
    // See GreeterClient for the client interface definition.
}
