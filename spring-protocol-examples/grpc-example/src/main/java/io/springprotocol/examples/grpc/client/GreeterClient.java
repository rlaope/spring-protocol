package io.springprotocol.examples.grpc.client;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;
import io.springprotocol.examples.grpc.proto.GreeterGrpc;
import io.springprotocol.examples.grpc.proto.HelloReply;
import io.springprotocol.examples.grpc.proto.HelloRequest;

@SpringClient(protocol = ProtocolType.GRPC, serviceId = "greeter-service", grpcClass = GreeterGrpc.class)
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @ProtocolMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}
