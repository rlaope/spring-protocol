package io.springprotocol.grpc.spring;

import io.springprotocol.grpc.spring.registrar.GrpcClientRegistrar;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables scanning for interfaces annotated with {@code @GrpcExchange}
 * and registers them as Spring beans via dynamic proxy.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GrpcClientRegistrar.class)
public @interface EnableGrpcClients {

    /**
     * Base packages to scan for @GrpcExchange interfaces.
     * Defaults to the package of the annotated class.
     */
    String[] basePackages() default {};
}
