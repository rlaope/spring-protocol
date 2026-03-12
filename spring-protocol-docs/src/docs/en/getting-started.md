# Getting Started

## Overview

Spring Protocol is a declarative client framework for Spring Boot that provides a unified, annotation-driven programming model for multiple communication protocols: gRPC, REST, GraphQL, and RSocket.

All protocols share the same two annotations:
- `@SpringClient` - marks an interface as a protocol client
- `@ProtocolMapping` - maps methods to protocol-specific operations

## Requirements

- Java 17 or later
- Spring Boot 3.x
- Gradle 8.x

## Installation

Add the unified boot starter and the protocol-specific starter you need:

```gradle
dependencies {
    // Always required
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'

    // Pick your protocol(s)
    implementation 'io.spring-protocol:grpc-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rest-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:graphql-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rsocket-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## Unified Configuration

```yaml
spring:
  protocol:
    grpc:
      clients:
        greeter-service:
          address: localhost:9090
    rest:
      clients:
        user-service:
          address: http://localhost:8081
    graphql:
      clients:
        user-service:
          address: http://localhost:8080/graphql
    rsocket:
      clients:
        notification-service:
          address: localhost:7000
```

## Core Annotations

### @SpringClient

Type-level annotation that marks an interface as a protocol client.

| Attribute   | Type           | Required | Description |
|-------------|----------------|----------|-------------|
| `protocol`  | `ProtocolType` | Yes      | `GRPC`, `REST`, `GRAPHQL`, or `RSOCKET` |
| `serviceId` | `String`       | Yes      | Maps to `spring.protocol.{protocol}.clients.{serviceId}.address` |
| `grpcClass` | `Class<?>`     | gRPC only | The generated gRPC class containing `newBlockingStub` |

### @ProtocolMapping

Method-level annotation that maps an interface method to a protocol operation.

| Attribute        | Protocols     | Description |
|------------------|---------------|-------------|
| `value`          | gRPC, REST    | gRPC: stub method name. REST: URL path (e.g., `/users/{id}`) |
| `method`         | REST          | HTTP method: `GET`, `POST`, `PUT`, `DELETE`, `PATCH` |
| `query`          | GraphQL       | GraphQL query or mutation string |
| `operationType`  | GraphQL       | `QUERY` (default) or `MUTATION` |
| `route`          | RSocket       | RSocket route (e.g., `notify`, `scores.stream`) |
| `interaction`    | RSocket       | `REQUEST_RESPONSE` (default), `FIRE_AND_FORGET`, `REQUEST_STREAM` |

## Project Structure

```
spring-protocol/
├── spring-protocol-core/           # Unified annotations (@SpringClient, @ProtocolMapping), SPI
├── spring-protocol-spring/         # @EnableSpringClients, bean registration
├── spring-protocol-boot/           # Auto-configuration, ProtocolRegistry
├── spring-protocol-grpc/           # gRPC client implementation
├── spring-protocol-rest/           # REST client implementation
├── spring-protocol-graphql/        # GraphQL client implementation
├── spring-protocol-rsocket/        # RSocket client implementation
├── spring-protocol-test/           # Test utilities
├── spring-protocol-examples/       # Usage examples per protocol
└── spring-protocol-docs/           # Documentation (this module)
```

## How It Works

1. `@EnableSpringClients` triggers classpath scanning for `@SpringClient` interfaces.
2. For each interface, a `SpringClientFactoryBean` is registered.
3. The factory bean resolves connection info from `spring.protocol.{protocol}.clients.{id}.address`.
4. The `ProtocolRegistry` dispatches to the correct `ProtocolClientHandler` (gRPC, REST, GraphQL, or RSocket).
5. The handler creates a JDK Dynamic Proxy backed by a protocol-specific `AbstractClientProxy` subclass.
6. Method metadata is cached in `ConcurrentHashMap` -- reflection happens only once per method.

## Protocol Guides

- [gRPC Quick Start](grpc/quick-start.md)
- [REST Quick Start](rest/quick-start.md)
- [GraphQL Quick Start](graphql/quick-start.md)
- [RSocket Quick Start](rsocket/quick-start.md)
