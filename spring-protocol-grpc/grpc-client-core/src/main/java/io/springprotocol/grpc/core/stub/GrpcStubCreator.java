package io.springprotocol.grpc.core.stub;

import io.grpc.Channel;

public interface GrpcStubCreator {

    Object create(Class<?> grpcClass, Channel channel);
}
