# Spring Protocol

A declarative client framework for Spring Boot that unifies multiple communication protocols behind a consistent annotation-driven programming model.

## Supported Protocols

| Protocol | Status | Description |
|----------|--------|-------------|
| gRPC     | Implemented | Blocking stub proxy with reflection-based stub factory |
| REST     | Skeleton | JDK HttpClient based REST client |
| GraphQL  | Skeleton | HTTP-based GraphQL query/mutation client |
| RSocket  | Skeleton | Reactive binary protocol client |

## Quick Start

### 1. Add Dependency

```gradle
dependencies {
    // Unified boot starter (required)
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'

    // Protocol-specific starter (pick what you need)
    implementation 'io.spring-protocol:grpc-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rest-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:graphql-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rsocket-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

### 2. Configure Target Services

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

### 3. Define Client Interfaces

```java
// gRPC Client
@SpringClient(protocol = ProtocolType.GRPC, serviceId = "greeter-service", grpcClass = GreeterGrpc.class)
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @ProtocolMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}

// REST Client
@SpringClient(protocol = ProtocolType.REST, serviceId = "user-service")
public interface UserClient {

    @ProtocolMapping(value = "/users/{id}", method = "GET")
    User getUser(String id);
}

// GraphQL Client
@SpringClient(protocol = ProtocolType.GRAPHQL, serviceId = "user-service")
public interface UserGraphqlClient {

    @ProtocolMapping(query = "{ user(id: $id) { name email } }")
    UserDto getUser(String id);
}

// RSocket Client
@SpringClient(protocol = ProtocolType.RSOCKET, serviceId = "notification-service")
public interface NotificationClient {

    @ProtocolMapping(route = "scores.stream", interaction = "REQUEST_STREAM")
    Flux<Score> streamScores(String matchId);
}
```

### 4. Enable and Inject

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "spring.protocol.client")
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

@Service
public class GreetingService {

    private final GreeterClient greeterClient;

    public GreetingService(GreeterClient greeterClient) {
        this.greeterClient = greeterClient;
    }

    public String greet(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        return greeterClient.sayHello(request).getMessage();
    }
}
```

## Architecture

```
spring-protocol/
├── spring-protocol-core/               # Unified annotations, SPI, proxy base
├── spring-protocol-spring/             # @EnableSpringClients, registrar, factory bean
├── spring-protocol-boot/               # Auto-configuration, unified properties
├── spring-protocol-grpc/               # gRPC implementation
│   ├── grpc-client-core
│   ├── grpc-client-spring
│   └── grpc-client-spring-boot-starter
├── spring-protocol-rest/               # REST implementation
│   ├── rest-client-core
│   ├── rest-client-spring
│   └── rest-client-spring-boot-starter
├── spring-protocol-graphql/            # GraphQL implementation
│   ├── graphql-client-core
│   ├── graphql-client-spring
│   └── graphql-client-spring-boot-starter
├── spring-protocol-rsocket/            # RSocket implementation
│   ├── rsocket-client-core
│   ├── rsocket-client-spring
│   └── rsocket-client-spring-boot-starter
├── spring-protocol-test/               # Test utilities
├── spring-protocol-examples/           # Usage examples per protocol
└── spring-protocol-docs/               # EN/KR documentation
```

### Design Principles

- **Unified API**: Single `@SpringClient` + `@ProtocolMapping` for all protocols. No protocol-specific annotations needed.
- **Protocol SPI**: `ProtocolClientHandler` interface allows pluggable protocol implementations auto-discovered via Spring Boot.
- **Separation of Concerns**: Core has zero Spring dependency. Protocol logic, Spring integration, and Boot auto-config are in separate modules.
- **Performance**: Method metadata and stub instances cached in `ConcurrentHashMap`. Reflection happens once per method.
- **Resilience**: Managed connections are shared per service and gracefully shut down on application stop.

## Requirements

- Java 17+
- Spring Boot 3.x

## Building

```bash
./gradlew build
```

## License

[Apache License 2.0](LICENSE)
