# GraphQL Client - Quick Start

## 1. Add Dependency

```gradle
dependencies {
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:graphql-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 2. Configure Target Endpoint

```yaml
spring:
  protocol:
    graphql:
      clients:
        user-service:
          address: http://localhost:8080/graphql
```

## 3. Define DTOs

```java
public class UserDto {
    private String id;
    private String name;
    private String email;

    // constructors, getters, setters
}
```

## 4. Declare Client Interface

```java
@SpringClient(protocol = ProtocolType.GRAPHQL, serviceId = "user-service")
public interface UserGraphqlClient {

    @ProtocolMapping(query = "query GetUser($id: ID!) { user(id: $id) { id name email } }")
    UserDto getUser(String id);

    @ProtocolMapping(
            query = "mutation CreateUser($name: String!, $email: String!) { createUser(name: $name, email: $email) { id name email } }",
            operationType = "MUTATION"
    )
    UserDto createUser(String name, String email);
}
```

- `protocol`: Set to `ProtocolType.GRAPHQL`.
- `serviceId`: Maps to `spring.protocol.graphql.clients.{serviceId}.address`.
- `@ProtocolMapping.query`: The full GraphQL query or mutation string.
- `@ProtocolMapping.operationType`: Optional. `QUERY` (default) or `MUTATION`.

### Variable Mapping

Method parameter names are automatically mapped to GraphQL variables:

```java
@ProtocolMapping(query = "query GetUser($id: ID!) { user(id: $id) { id name } }")
UserDto getUser(String id);
// Method call: getUser("123")
// Sent as: {"query": "...", "variables": {"id": "123"}}
```

Multiple variables work the same way:

```java
@ProtocolMapping(query = "mutation CreateUser($name: String!, $email: String!) { ... }")
UserDto createUser(String name, String email);
// Sent as: {"query": "...", "variables": {"name": "John", "email": "john@example.com"}}
```

### Response Unwrapping

The framework automatically unwraps the GraphQL `data` envelope. If the `data` object has a single field, its value is deserialized directly into the return type:

```json
// Server response:
{"data": {"user": {"id": "1", "name": "John", "email": "john@example.com"}}}

// Automatically unwrapped to UserDto
```

## 5. Enable Scanning

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.graphql.client")
public class GraphqlExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlExampleApplication.class, args);
    }
}
```

## 6. Inject and Use

```java
@Service
public class UserService {

    private final UserGraphqlClient userGraphqlClient;

    public UserService(UserGraphqlClient userGraphqlClient) {
        this.userGraphqlClient = userGraphqlClient;
    }

    public UserDto findUser(String id) {
        return userGraphqlClient.getUser(id);
    }

    public UserDto registerUser(String name, String email) {
        return userGraphqlClient.createUser(name, email);
    }
}
```

## Implementation Details

- Sends queries via HTTP POST to the configured endpoint.
- Request body format: `{"query": "...", "variables": {...}, "operationName": "..."}`.
- JSON handling powered by Jackson `ObjectMapper`.
- GraphQL errors in the response (`errors` field) throw `GraphqlClientException`.
- `Content-Type: application/json` and `Accept: application/json` headers are set automatically.
- Proxy instances are cached per service.

## Error Handling

```java
try {
    UserDto user = userGraphqlClient.getUser("unknown");
} catch (GraphqlClientException e) {
    System.out.println(e.getStatusCode());    // HTTP status or 200 for GraphQL errors
    System.out.println(e.getResponseBody());  // GraphQL error details
}
```

## Multiple Services

```java
@SpringClient(protocol = ProtocolType.GRAPHQL, serviceId = "product-service")
public interface ProductGraphqlClient {

    @ProtocolMapping(query = "query GetProduct($id: ID!) { product(id: $id) { id name price } }")
    ProductDto getProduct(String id);
}
```

```yaml
spring:
  protocol:
    graphql:
      clients:
        user-service:
          address: http://localhost:8080/graphql
        product-service:
          address: http://localhost:8090/graphql
```
