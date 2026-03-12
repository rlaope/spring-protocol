package io.springprotocol.spring;

import io.springprotocol.spring.registrar.SpringClientRegistrar;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables scanning for interfaces annotated with {@code @SpringClient}
 * and registers them as Spring beans via protocol-specific dynamic proxies.
 *
 * <pre>
 * {@literal @}SpringBootApplication
 * {@literal @}EnableSpringClients(basePackages = "spring.protocol.client")
 * public class MyApplication { ... }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringClientRegistrar.class)
public @interface EnableSpringClients {

    /**
     * Base packages to scan for {@code @SpringClient} interfaces.
     * Defaults to the package of the annotated class.
     */
    String[] basePackages() default {};
}
