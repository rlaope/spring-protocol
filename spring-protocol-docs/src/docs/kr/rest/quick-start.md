# REST Client - 빠른 시작

## 1. 의존성 추가

```gradle
dependencies {
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rest-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 2. 대상 주소 설정

```yaml
spring:
  protocol:
    rest:
      clients:
        user-service:
          address: http://localhost:8081
```

## 3. DTO 정의

```java
public class User {
    private String id;
    private String name;
    private String email;

    // 생성자, getter, setter
}

public class CreateUserRequest {
    private String name;
    private String email;

    // 생성자, getter, setter
}
```

## 4. 클라이언트 인터페이스 선언

```java
@SpringClient(protocol = ProtocolType.REST, serviceId = "user-service")
public interface UserClient {

    @ProtocolMapping(value = "/users/{id}", method = "GET")
    User getUser(String id);

    @ProtocolMapping(value = "/users", method = "POST")
    User createUser(CreateUserRequest request);

    @ProtocolMapping(value = "/users/{id}", method = "DELETE")
    void deleteUser(String id);
}
```

- `protocol`: `ProtocolType.REST`로 설정합니다.
- `serviceId`: 설정 파일의 `spring.protocol.rest.clients.{serviceId}.address`에 매핑됩니다.
- `@ProtocolMapping.value`: URL 경로. `{variable}` 플레이스홀더를 지원하며 메서드 인자 순서대로 치환됩니다.
- `@ProtocolMapping.method`: HTTP 메서드 — `GET`, `POST`, `PUT`, `DELETE`, `PATCH`.

### Path Variable

Path variable은 메서드 파라미터 순서대로 치환됩니다:

```java
@ProtocolMapping(value = "/users/{userId}/posts/{postId}", method = "GET")
Post getPost(String userId, String postId);
// userId → {userId}, postId → {postId}
```

### Request Body

`POST`, `PUT`, `PATCH`의 경우 path variable 이후의 첫 번째 인자가 JSON request body가 됩니다:

```java
@ProtocolMapping(value = "/users/{id}", method = "PUT")
User updateUser(String id, UpdateUserRequest body);
// id → path variable, body → JSON request body
```

## 5. 스캐닝 활성화

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.rest.client")
public class RestExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestExampleApplication.class, args);
    }
}
```

## 6. 주입 및 사용

```java
@Service
public class UserService {

    private final UserClient userClient;

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public User findUser(String id) {
        return userClient.getUser(id);
    }

    public User registerUser(String name, String email) {
        CreateUserRequest request = new CreateUserRequest(name, email);
        return userClient.createUser(request);
    }

    public void removeUser(String id) {
        userClient.deleteUser(id);
    }
}
```

## 구현 세부사항

- 내부적으로 JDK `HttpClient`를 사용합니다 — 외부 HTTP 라이브러리가 필요 없습니다.
- JSON 직렬화/역직렬화는 Jackson `ObjectMapper`로 처리됩니다.
- `Content-Type: application/json`과 `Accept: application/json` 헤더가 자동 설정됩니다.
- HTTP 4xx/5xx 응답은 상태 코드와 응답 본문을 포함한 `RestClientException`을 발생시킵니다.
- 프록시 인스턴스와 `HttpClient`는 서비스별로 캐싱됩니다.

## 에러 처리

```java
try {
    User user = userClient.getUser("unknown-id");
} catch (RestClientException e) {
    System.out.println(e.getStatusCode());    // 404
    System.out.println(e.getResponseBody());  // {"error": "Not found"}
}
```

## 다중 서비스

```java
@SpringClient(protocol = ProtocolType.REST, serviceId = "order-service")
public interface OrderClient {

    @ProtocolMapping(value = "/orders", method = "POST")
    Order createOrder(CreateOrderRequest request);

    @ProtocolMapping(value = "/orders/{id}", method = "GET")
    Order getOrder(String id);
}
```

```yaml
spring:
  protocol:
    rest:
      clients:
        user-service:
          address: http://localhost:8081
        order-service:
          address: http://localhost:8082
```
