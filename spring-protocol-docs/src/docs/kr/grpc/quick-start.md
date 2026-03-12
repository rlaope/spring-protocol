# gRPC Client - 빠른 시작

## 1. Proto 정의

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

protobuf Gradle 플러그인으로 Java 스텁을 생성합니다. `GreeterGrpc` 클래스와 `newBlockingStub(Channel)` 메서드가 생성됩니다.

## 2. 대상 주소 설정

```yaml
# application.yml
grpc:
  client:
    greeter-service:
      address: localhost:9090
    order-service:
      address: order-svc.internal:9091
```

## 3. 클라이언트 인터페이스 선언

```java
@GrpcExchange(grpcClass = GreeterGrpc.class, serviceId = "greeter-service")
public interface GreeterClient {

    HelloReply sayHello(HelloRequest request);

    @GrpcMapping("sayHelloAgain")
    HelloReply greetAgain(HelloRequest request);
}
```

- `grpcClass`: `newBlockingStub`이 포함된 생성된 gRPC 클래스를 지정합니다.
- `serviceId`: 설정 파일의 `grpc.client.{serviceId}.address`에 매핑됩니다.
- `@GrpcMapping`: 선택사항. 인터페이스 메서드명과 스텁 메서드명이 다를 때 사용합니다.

## 4. 스캐닝 활성화

```java
@SpringBootApplication
@EnableGrpcClients(basePackages = "spring.protocol.client")
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

## 동작 원리

1. `@EnableGrpcClients`가 `GrpcClientRegistrar`를 트리거하여 `@GrpcExchange` 인터페이스를 스캔합니다.
2. 각 인터페이스마다 `GrpcClientFactoryBean`이 Spring 빈으로 등록됩니다.
3. 최초 접근 시 FactoryBean이:
   - `Environment`에서 대상 주소를 확인합니다
   - `ManagedChannel`을 생성하거나 기존 채널을 재사용합니다 (주소별 공유)
   - 리플렉션으로 blocking stub을 생성합니다 (`GreeterGrpc.newBlockingStub(channel)`)
   - 인터페이스를 구현하는 JDK Dynamic Proxy로 스텁을 래핑합니다
4. 프록시의 메서드 호출은 캐싱된 스텁 메서드로 디스패치됩니다. 메타데이터는 한 번만 리졸브되어 `ConcurrentHashMap`에 캐싱됩니다.

## 다중 서비스 예시

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
        // 고객에게 인사
        HelloRequest helloReq = HelloRequest.newBuilder()
                .setName(customerName)
                .build();
        greeterClient.sayHello(helloReq);

        // 주문 생성
        return orderClient.createOrder(orderReq);
    }
}
```
