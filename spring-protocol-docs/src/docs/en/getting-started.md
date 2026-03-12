# Getting Started

## Overview

Spring Protocol is a declarative client framework for Spring Boot that provides a unified, annotation-driven programming model for multiple communication protocols including gRPC, GraphQL, REST, and RSocket.

## Requirements

- Java 17 or later
- Spring Boot 3.x
- Gradle 8.x

## Installation

Add the starter dependency for the protocol you need:

### gRPC Client

```gradle
dependencies {
    implementation 'io.spring-protocol:grpc-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## Project Structure

```
spring-protocol
├── spring-protocol-grpc/           # gRPC client modules
│   ├── grpc-client-core            # Annotations, proxy, stub caching
│   ├── grpc-client-spring          # Spring bean registration
│   └── grpc-client-spring-boot-starter  # Auto-configuration
├── spring-protocol-test/           # Test utilities
├── spring-protocol-examples/       # Usage examples
└── spring-protocol-docs/           # Documentation (this module)
```

## Module Dependency Graph

```
spring-boot-starter → grpc-client-spring → grpc-client-core
```

Each module has a clear responsibility:

- **grpc-client-core**: Zero Spring dependency. Contains annotations, JDK Dynamic Proxy logic, stub creation via reflection, and channel management.
- **grpc-client-spring**: Spring integration layer. Registers `@GrpcExchange` interfaces as Spring beans using `ImportBeanDefinitionRegistrar`.
- **grpc-client-spring-boot-starter**: Spring Boot auto-configuration. Reads `application.yml` properties and wires infrastructure beans.
