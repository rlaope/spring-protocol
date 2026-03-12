package io.springprotocol.grpc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an interface method to a specific gRPC stub method.
 * If not specified, the framework uses the interface method name
 * to locate the corresponding stub method by convention.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcMapping {

    /**
     * The name of the gRPC stub method to invoke.
     * Defaults to the annotated method's name.
     */
    String value() default "";
}
