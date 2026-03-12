package io.springprotocol.examples.grpc.client;

import io.springprotocol.examples.grpc.proto.GreeterGrpc;
import io.springprotocol.examples.grpc.proto.HelloReply;
import io.springprotocol.examples.grpc.proto.HelloRequest;
import io.springprotocol.grpc.core.annotation.GrpcExchange;
import io.springprotocol.grpc.core.annotation.GrpcMapping;

@GrpcExchange(grpcClass = GreeterGrpc.class, serviceId = "greeter-service")
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @GrpcMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}
