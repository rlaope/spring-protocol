# GraphQL Client - 빠른 시작

## 1. 의존성 추가

```gradle
dependencies {
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:graphql-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 2. 대상 엔드포인트 설정

```yaml
spring:
  protocol:
    graphql:
      clients:
        user-service:
          address: http://localhost:8080/graphql
```

## 3. DTO 정의

```java
public class UserDto {
    private String id;
    private String name;
    private String email;

    // 생성자, getter, setter
}
```

## 4. 클라이언트 인터페이스 선언

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

- `protocol`: `ProtocolType.GRAPHQL`로 설정합니다.
- `serviceId`: 설정 파일의 `spring.protocol.graphql.clients.{serviceId}.address`에 매핑됩니다.
- `@ProtocolMapping.query`: 전체 GraphQL 쿼리 또는 뮤테이션 문자열.
- `@ProtocolMapping.operationType`: 선택사항. `QUERY` (기본값) 또는 `MUTATION`.

### Variable 매핑

메서드 파라미터 이름이 자동으로 GraphQL 변수에 매핑됩니다:

```java
@ProtocolMapping(query = "query GetUser($id: ID!) { user(id: $id) { id name } }")
UserDto getUser(String id);
// 메서드 호출: getUser("123")
// 전송 형태: {"query": "...", "variables": {"id": "123"}}
```

다중 변수도 동일하게 동작합니다:

```java
@ProtocolMapping(query = "mutation CreateUser($name: String!, $email: String!) { ... }")
UserDto createUser(String name, String email);
// 전송 형태: {"query": "...", "variables": {"name": "John", "email": "john@example.com"}}
```

### 응답 언래핑

프레임워크가 GraphQL `data` 엔벨로프를 자동으로 언래핑합니다. `data` 객체에 단일 필드만 있으면 그 값이 직접 리턴 타입으로 역직렬화됩니다:

```json
// 서버 응답:
{"data": {"user": {"id": "1", "name": "John", "email": "john@example.com"}}}

// UserDto로 자동 언래핑
```

## 5. 스캐닝 활성화

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.graphql.client")
public class GraphqlExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlExampleApplication.class, args);
    }
}
```

## 6. 주입 및 사용

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

## 구현 세부사항

- HTTP POST로 설정된 엔드포인트에 쿼리를 전송합니다.
- 요청 본문 형식: `{"query": "...", "variables": {...}, "operationName": "..."}`.
- JSON 처리는 Jackson `ObjectMapper`로 수행됩니다.
- 응답의 GraphQL 에러 (`errors` 필드)는 `GraphqlClientException`을 발생시킵니다.
- `Content-Type: application/json`과 `Accept: application/json` 헤더가 자동 설정됩니다.
- 프록시 인스턴스는 서비스별로 캐싱됩니다.

## 에러 처리

```java
try {
    UserDto user = userGraphqlClient.getUser("unknown");
} catch (GraphqlClientException e) {
    System.out.println(e.getStatusCode());    // HTTP 상태 코드 또는 GraphQL 에러 시 200
    System.out.println(e.getResponseBody());  // GraphQL 에러 세부 정보
}
```

## 다중 서비스

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
