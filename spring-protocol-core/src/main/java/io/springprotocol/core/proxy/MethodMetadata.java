package io.springprotocol.core.proxy;

/**
 * Cached metadata resolved from a method's {@code @ProtocolMapping} annotation.
 * Immutable and protocol-agnostic -- each protocol handler reads only what it needs.
 */
public record MethodMetadata(
        String mappedName,
        String method,
        String query,
        String operationType,
        String route,
        String interaction,
        Class<?> returnType,
        Class<?>[] parameterTypes
) {
}
