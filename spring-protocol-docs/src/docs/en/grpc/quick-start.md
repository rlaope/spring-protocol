# gRPC Client - Quick Start

## 1. Define Your Proto

```protobuf
syntax = "proto3";
option java_package = "com.example.proto";

service Greeter {
  rpc SayHello (HelloRequest) returns (HelloReply);
  rpc SayHelloAgain (HelloRequest) returns (HelloReply);
}

message HelloRequest { string name = 1; }
message HelloReply { string message = 1; }
```

## 2. Configure Target Address

```yaml
spring:
  protocol:
    grpc:
      clients:
        greeter-service:
          address: localhost:9090
```

## 3. Declare Client Interface

```java
@SpringClient(protocol = ProtocolType.GRPC, serviceId = "greeter-service", grpcClass = GreeterGrpc.class)
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @ProtocolMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}
```

- `protocol`: Set to `ProtocolType.GRPC`.
- `grpcClass`: The generated gRPC class containing `newBlockingStub`.
- `serviceId`: Maps to `spring.protocol.grpc.clients.{serviceId}.address`.
- `@ProtocolMapping`: Optional. Use when the interface method name differs from the stub method name.

## 4. Enable Scanning

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "spring.protocol.client")
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
        return greeterClient.sayHello(request).getMessage();
    }
}
```

## Multiple Services

```java
@SpringClient(protocol = ProtocolType.GRPC, serviceId = "order-service", grpcClass = OrderGrpc.class)
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
        greeterClient.sayHello(HelloRequest.newBuilder().setName(customerName).build());
        return orderClient.createOrder(orderReq);
    }
}
```
