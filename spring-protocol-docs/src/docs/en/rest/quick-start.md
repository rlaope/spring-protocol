# REST Client - Quick Start

## 1. Add Dependency

```gradle
dependencies {
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rest-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 2. Configure Target Address

```yaml
spring:
  protocol:
    rest:
      clients:
        user-service:
          address: http://localhost:8081
```

## 3. Define DTOs

```java
public class User {
    private String id;
    private String name;
    private String email;

    // constructors, getters, setters
}

public class CreateUserRequest {
    private String name;
    private String email;

    // constructors, getters, setters
}
```

## 4. Declare Client Interface

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

- `protocol`: Set to `ProtocolType.REST`.
- `serviceId`: Maps to `spring.protocol.rest.clients.{serviceId}.address`.
- `@ProtocolMapping.value`: The URL path. Supports `{variable}` placeholders resolved from method arguments in order.
- `@ProtocolMapping.method`: HTTP method — `GET`, `POST`, `PUT`, `DELETE`, `PATCH`.

### Path Variables

Path variables are resolved positionally from method parameters:

```java
@ProtocolMapping(value = "/users/{userId}/posts/{postId}", method = "GET")
Post getPost(String userId, String postId);
// userId → {userId}, postId → {postId}
```

### Request Body

For `POST`, `PUT`, `PATCH` — the first argument after path variables becomes the JSON request body:

```java
@ProtocolMapping(value = "/users/{id}", method = "PUT")
User updateUser(String id, UpdateUserRequest body);
// id → path variable, body → JSON request body
```

## 5. Enable Scanning

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.rest.client")
public class RestExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestExampleApplication.class, args);
    }
}
```

## 6. Inject and Use

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

## Implementation Details

- Uses JDK `HttpClient` internally — no external HTTP library required.
- JSON serialization/deserialization powered by Jackson `ObjectMapper`.
- `Content-Type: application/json` and `Accept: application/json` headers are set automatically.
- HTTP 4xx/5xx responses throw `RestClientException` with status code and response body.
- Proxy instances and `HttpClient` are cached per service — no redundant object creation.

## Error Handling

```java
try {
    User user = userClient.getUser("unknown-id");
} catch (RestClientException e) {
    System.out.println(e.getStatusCode());    // 404
    System.out.println(e.getResponseBody());  // {"error": "Not found"}
}
```

## Multiple Services

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
