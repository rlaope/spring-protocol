package io.springprotocol.grpc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface as a gRPC client proxy.
 * The framework creates a dynamic proxy that delegates calls to the actual gRPC stub.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcExchange {

    /**
     * The generated gRPC class (e.g., GreeterGrpc.class) that contains
     * the static {@code newBlockingStub} factory method.
     */
    Class<?> grpcClass();

    /**
     * The logical service ID used to resolve the target address
     * from configuration (grpc.client.{serviceId}.address).
     */
    String serviceId();
}
