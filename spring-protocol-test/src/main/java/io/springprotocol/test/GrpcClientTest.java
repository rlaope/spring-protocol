package io.springprotocol.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to simplify gRPC client integration testing.
 * Can be used on test classes to auto-configure an in-process gRPC server
 * and wire client proxies against it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcClientTest {

    /**
     * The gRPC client interfaces to create for this test.
     */
    Class<?>[] clients() default {};
}
