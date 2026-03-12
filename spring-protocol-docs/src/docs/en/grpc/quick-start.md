# gRPC Client - Quick Start

## 1. Define Your Proto

```protobuf
syntax = "proto3";

package example;

service Greeter {
  rpc SayHello (HelloRequest) returns (HelloReply);
  rpc SayHelloAgain (HelloRequest) returns (HelloReply);
}

message HelloRequest {
  string name = 1;
}

message HelloReply {
  string message = 1;
}
```

Generate Java stubs using the protobuf Gradle plugin. This produces `GreeterGrpc` with `newBlockingStub(Channel)`.

## 2. Configure Target Address

```yaml
# application.yml
grpc:
  client:
    greeter-service:
      address: localhost:9090
    order-service:
      address: order-svc.internal:9091
```

## 3. Declare Client Interface

```java
@GrpcExchange(grpcClass = GreeterGrpc.class, serviceId = "greeter-service")
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @GrpcMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}
```

- `grpcClass`: Points to the generated gRPC class containing `newBlockingStub`.
- `serviceId`: Maps to `grpc.client.{serviceId}.address` in configuration.
- `@GrpcMapping`: Optional. Use when the interface method name differs from the stub method name.

## 4. Enable Scanning

```java
@SpringBootApplication
@EnableGrpcClients(basePackages = "spring.protocol.client")
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

## 5. Inject and Use

```java
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

    public String greetAgain(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        HelloReply reply = greeterClient.greetAgain(request);
        return reply.getMessage();
    }
}
```

## How It Works

1. `@EnableGrpcClients` triggers `GrpcClientRegistrar` which scans for `@GrpcExchange` interfaces.
2. For each interface, a `GrpcClientFactoryBean` is registered as a Spring bean.
3. On first access, the factory bean:
   - Resolves the target address from `Environment`
   - Gets or creates a `ManagedChannel` (shared per address)
   - Creates a blocking stub via reflection (`GreeterGrpc.newBlockingStub(channel)`)
   - Wraps the stub in a JDK Dynamic Proxy implementing the interface
4. Method calls on the proxy are dispatched to the cached stub method. Metadata is resolved once and cached in `ConcurrentHashMap`.

## Multiple Services Example

```java
@GrpcExchange(grpcClass = OrderGrpc.class, serviceId = "order-service")
public interface OrderClient {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrder(GetOrderRequest request);
}

@Service
public class OrderFacade {

    private final GreeterClient greeterClient;
    private final OrderClient orderClient;

    public OrderFacade(GreeterClient greeterClient, OrderClient orderClient) {
        this.greeterClient = greeterClient;
        this.orderClient = orderClient;
    }

    public OrderResponse placeOrder(String customerName, CreateOrderRequest orderReq) {
        // Greet the customer
        HelloRequest helloReq = HelloRequest.newBuilder()
                .setName(customerName)
                .build();
        greeterClient.sayHello(helloReq);

        // Place the order
        return orderClient.createOrder(orderReq);
    }
}
```
