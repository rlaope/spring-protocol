package io.springprotocol.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Unified annotation that marks an interface as a Spring Protocol client.
 * Supports multiple protocols through a single consistent programming model.
 *
 * <pre>
 * {@literal @}SpringClient(protocol = ProtocolType.GRPC, serviceId = "greeter-service", grpcClass = GreeterGrpc.class)
 * public interface GreeterClient { ... }
 *
 * {@literal @}SpringClient(protocol = ProtocolType.REST, serviceId = "payment-service")
 * public interface PaymentClient { ... }
 *
 * {@literal @}SpringClient(protocol = ProtocolType.GRAPHQL, serviceId = "user-service")
 * public interface UserClient { ... }
 *
 * {@literal @}SpringClient(protocol = ProtocolType.RSOCKET, serviceId = "notification-service")
 * public interface NotificationClient { ... }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpringClient {

    /**
     * The protocol type.
     */
    ProtocolType protocol();

    /**
     * The logical service ID used to resolve connection info
     * from configuration (spring.protocol.{protocol}.clients.{serviceId}.address).
     */
    String serviceId();

    /**
     * The generated gRPC class containing {@code newBlockingStub(Channel)}.
     * Only required when protocol = GRPC.
     */
    Class<?> grpcClass() default void.class;
}
