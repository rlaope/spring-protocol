# gRPC Client - 빠른 시작

## 1. Proto 정의

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

## 2. 대상 주소 설정

```yaml
spring:
  protocol:
    grpc:
      clients:
        greeter-service:
          address: localhost:9090
```

## 3. 클라이언트 인터페이스 선언

```java
@SpringClient(protocol = ProtocolType.GRPC, serviceId = "greeter-service", grpcClass = GreeterGrpc.class)
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @ProtocolMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}
```

- `protocol`: `ProtocolType.GRPC`로 설정합니다.
- `grpcClass`: `newBlockingStub`이 포함된 생성된 gRPC 클래스를 지정합니다.
- `serviceId`: 설정 파일의 `spring.protocol.grpc.clients.{serviceId}.address`에 매핑됩니다.
- `@ProtocolMapping`: 선택사항. 인터페이스 메서드명과 스텁 메서드명이 다를 때 사용합니다.

## 4. 스캐닝 활성화

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "spring.protocol.client")
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

## 5. 주입 및 사용

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

## 다중 서비스

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
