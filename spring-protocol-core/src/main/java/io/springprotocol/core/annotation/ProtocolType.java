package io.springprotocol.core.annotation;

/**
 * Supported protocol types for Spring Protocol clients.
 */
public enum ProtocolType {

    /** gRPC protocol using blocking stubs */
    GRPC,

    /** GraphQL over HTTP */
    GRAPHQL,

    /** RESTful HTTP */
    REST,

    /** RSocket binary protocol */
    RSOCKET
}
