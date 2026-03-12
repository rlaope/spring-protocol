package io.springprotocol.grpc.core.proxy;

import java.lang.reflect.Method;

/**
 * Cached metadata for a gRPC stub method invocation.
 * Avoids repeated reflection lookups on every call.
 */
public record GrpcMethodMetadata(
        Method stubMethod,
        Class<?> requestType
) {
}
