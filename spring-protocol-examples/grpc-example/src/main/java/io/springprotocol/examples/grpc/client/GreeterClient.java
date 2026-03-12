package io.springprotocol.examples.grpc.client;

import io.springprotocol.grpc.core.annotation.GrpcExchange;
import io.springprotocol.grpc.core.annotation.GrpcMapping;

/**
 * Example gRPC client interface.
 *
 * <p>The {@code grpcClass} points to the generated gRPC class (e.g., GreeterGrpc),
 * and {@code serviceId} maps to the configuration key in application.yml.</p>
 *
 * <p>Usage assumes a proto-generated GreeterGrpc class exists with a
 * {@code newBlockingStub(Channel)} method.</p>
 *
 * <pre>
 * // Example proto definition:
 * // service Greeter {
 * //   rpc SayHello (HelloRequest) returns (HelloReply);
 * // }
 * </pre>
 */
// @GrpcExchange(grpcClass = GreeterGrpc.class, serviceId = "greeter-service")
public interface GreeterClient {

    // HelloReply sayHello(HelloRequest request);

    // @GrpcMapping("sayHelloAgain")
    // HelloReply sayHelloAgain(HelloRequest request);
}
