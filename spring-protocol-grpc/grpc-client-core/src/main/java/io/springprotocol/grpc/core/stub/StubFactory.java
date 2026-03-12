package io.springprotocol.grpc.core.stub;

import io.grpc.Channel;

/**
 * Creates gRPC blocking stubs from a generated Grpc class via reflection.
 */
public interface StubFactory {

    /**
     * Creates a blocking stub instance for the given gRPC class using the provided channel.
     *
     * @param grpcClass the generated gRPC class (e.g., GreeterGrpc.class)
     * @param channel   the managed channel to bind the stub to
     * @return the blocking stub instance
     */
    Object createStub(Class<?> grpcClass, Channel channel);
}
