package io.springprotocol.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Unified method-level mapping annotation for all protocols.
 * Each protocol handler reads only the attributes relevant to its protocol type.
 *
 * <pre>
 * // gRPC: maps to stub method name
 * {@literal @}ProtocolMapping("sayHello")
 * HelloReply sayHello(HelloRequest request);
 *
 * // REST: maps to HTTP endpoint
 * {@literal @}ProtocolMapping(value = "/users/{id}", method = "GET")
 * User getUser(String id);
 *
 * // GraphQL: maps to query with inline query string
 * {@literal @}ProtocolMapping(query = "{ user(id: $id) { name email } }")
 * UserDto getUserFields(String id);
 *
 * // RSocket: real-time streaming
 * {@literal @}ProtocolMapping(route = "scores.stream", interaction = "REQUEST_STREAM")
 * Flux&lt;Score&gt; streamScores(String matchId);
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProtocolMapping {

    /**
     * The target name: stub method name (gRPC), path (REST),
     * operation name (GraphQL), or route (RSocket).
     * Defaults to the annotated method's name.
     */
    String value() default "";

    // ── REST ──

    /**
     * HTTP method for REST protocol (GET, POST, PUT, DELETE, PATCH).
     */
    String method() default "";

    // ── GraphQL ──

    /**
     * GraphQL query/mutation/subscription string.
     */
    String query() default "";

    /**
     * GraphQL operation type: QUERY, MUTATION, SUBSCRIPTION.
     * Defaults to QUERY.
     */
    String operationType() default "";

    // ── RSocket ──

    /**
     * RSocket route. If empty, falls back to {@link #value()}.
     */
    String route() default "";

    /**
     * RSocket interaction model: REQUEST_RESPONSE, FIRE_AND_FORGET,
     * REQUEST_STREAM, REQUEST_CHANNEL.
     * Defaults to REQUEST_RESPONSE.
     */
    String interaction() default "";
}
