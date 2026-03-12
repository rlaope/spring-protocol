# Spring Protocol

A declarative client framework for Spring Boot that unifies multiple communication protocols (gRPC, GraphQL, REST, RSocket) behind a consistent annotation-driven programming model.

## Modules

### gRPC Client (`spring-protocol-grpc`)

Declarative gRPC client inspired by Spring Cloud OpenFeign. Define interfaces with annotations and let Spring Protocol handle stub creation, channel management, and method dispatch automatically.

| Module | Description |
|--------|-------------|
| `grpc-client-core` | Annotations (`@GrpcExchange`, `@GrpcMapping`), JDK Dynamic Proxy, stub creation & caching |
| `grpc-client-spring` | `ImportBeanDefinitionRegistrar` for automatic bean scanning and registration |
| `grpc-client-spring-boot-starter` | Auto-configuration and `application.yml` property binding |

## Quick Start

### 1. Add Dependency

```gradle
dependencies {
    implementation 'io.spring-protocol:grpc-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

### 2. Configure Target Address

```yaml
grpc:
  client:
    greeter-service:
      address: localhost:9090
```

### 3. Define Client Interface

```java
@GrpcExchange(grpcClass = GreeterGrpc.class, serviceId = "greeter-service")
public interface GreeterClient {

    @GrpcMapping("sayHello")
    HelloReply sayHello(HelloRequest request);
}
```

### 4. Enable and Inject

```java
@SpringBootApplication
@EnableGrpcClients(basePackages = "com.example.client")
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
        HelloReply reply = greeterClient.sayHello(request);
        return reply.getMessage();
    }
}
```

## Architecture

```
spring-protocol
├── spring-protocol-grpc
│   ├── grpc-client-core          # Zero Spring dependency
│   ├── grpc-client-spring        # Spring integration layer
│   └── grpc-client-spring-boot-starter  # Auto-configuration
├── spring-protocol-graphql       # (planned)
├── spring-protocol-rest          # (planned)
└── spring-protocol-rsocket       # (planned)
```

### Design Principles

- **Separation of Concerns**: Core proxy logic has no Spring dependency. Spring integration and Boot auto-config are in separate modules.
- **Performance**: Stub instances and method metadata are cached in `ConcurrentHashMap`. Reflection happens once per method, not per call.
- **Resilience**: `ManagedChannel` instances are shared per service address and gracefully shut down on application stop.
- **Convention over Configuration**: Method names map to stub methods by default. Use `@GrpcMapping` only when names differ.

## Requirements

- Java 17+
- Spring Boot 3.x
- gRPC stubs generated from `.proto` files

## Building

```bash
./gradlew build
```

## License

[Apache License 2.0](LICENSE)
