# RSocket Client - 빠른 시작

## 1. 의존성 추가

```gradle
dependencies {
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rsocket-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 2. 대상 주소 설정

```yaml
spring:
  protocol:
    rsocket:
      clients:
        notification-service:
          address: localhost:7000
```

## 3. DTO 정의

```java
public class NotificationRequest {
    private String userId;
    private String message;
    // 생성자, getter, setter
}

public class NotificationResponse {
    private String status;
    private String notificationId;
    // 생성자, getter, setter
}

public class Score {
    private String matchId;
    private String team;
    private int points;
    // 생성자, getter, setter
}
```

## 4. 클라이언트 인터페이스 선언

```java
@SpringClient(protocol = ProtocolType.RSOCKET, serviceId = "notification-service")
public interface NotificationClient {

    @ProtocolMapping(route = "notify", interaction = "REQUEST_RESPONSE")
    NotificationResponse sendNotification(NotificationRequest request);

    @ProtocolMapping(route = "notify.fire", interaction = "FIRE_AND_FORGET")
    void fireNotification(NotificationRequest request);

    @ProtocolMapping(route = "scores.stream", interaction = "REQUEST_STREAM")
    Flux<Score> streamScores(String matchId);
}
```

- `protocol`: `ProtocolType.RSOCKET`으로 설정합니다.
- `serviceId`: 설정 파일의 `spring.protocol.rsocket.clients.{serviceId}.address`에 매핑됩니다.
- `@ProtocolMapping.route`: RSocket 라우트 문자열.
- `@ProtocolMapping.interaction`: 상호작용 모델.

### 상호작용 모델

| 상호작용 | 리턴 타입 | 설명 |
|----------|----------|------|
| `REQUEST_RESPONSE` | `T` | 하나의 메시지를 보내고 하나의 응답을 받습니다. 생략 시 기본값. |
| `FIRE_AND_FORGET` | `void` | 하나의 메시지를 보내고 응답을 기다리지 않습니다. |
| `REQUEST_STREAM` | `Flux<T>` | 하나의 메시지를 보내고 응답 스트림을 받습니다. |

### 페이로드 직렬화

- 첫 번째 메서드 인자가 JSON으로 직렬화되어 데이터 페이로드로 전송됩니다.
- 라우트 문자열은 메타데이터로 전송됩니다.
- 응답은 Jackson을 사용하여 JSON에서 역직렬화됩니다.

## 5. 스캐닝 활성화

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.rsocket.client")
public class RSocketExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RSocketExampleApplication.class, args);
    }
}
```

## 6. 주입 및 사용

```java
@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public NotificationResponse notify(String userId, String message) {
        NotificationRequest request = new NotificationRequest(userId, message);
        return notificationClient.sendNotification(request);
    }

    public void fireAndForget(String userId, String message) {
        NotificationRequest request = new NotificationRequest(userId, message);
        notificationClient.fireNotification(request);
    }

    public Flux<Score> watchScores(String matchId) {
        return notificationClient.streamScores(matchId);
    }
}
```

## 구현 세부사항

- `rsocket-java`를 TCP 트랜스포트(`rsocket-transport-netty`)와 함께 사용합니다.
- 연결은 지연 생성되며 주소별로 캐싱됩니다 — 서비스당 하나의 TCP 연결.
- JSON 직렬화/역직렬화는 Jackson `ObjectMapper`로 처리됩니다.
- `REQUEST_RESPONSE`는 `RSocket.requestResponse()`를 호출하고 결과를 블로킹합니다.
- `FIRE_AND_FORGET`은 `RSocket.fireAndForget()`을 호출합니다 — 즉시 반환.
- `REQUEST_STREAM`은 `RSocket.requestStream()`을 호출합니다 — 리액티브 `Flux<T>` 반환.
- 애플리케이션 종료 시 `ProtocolClientHandler.destroy()`를 통해 연결이 해제됩니다.

## 에러 처리

연결 실패와 직렬화 오류는 `RSocketClientException`을 발생시킵니다:

```java
try {
    NotificationResponse response = notificationClient.sendNotification(request);
} catch (RSocketClientException e) {
    System.out.println(e.getMessage());
}
```

## 다중 서비스

```java
@SpringClient(protocol = ProtocolType.RSOCKET, serviceId = "chat-service")
public interface ChatClient {

    @ProtocolMapping(route = "chat.send", interaction = "FIRE_AND_FORGET")
    void sendMessage(ChatMessage message);

    @ProtocolMapping(route = "chat.stream", interaction = "REQUEST_STREAM")
    Flux<ChatMessage> streamMessages(String roomId);
}
```

```yaml
spring:
  protocol:
    rsocket:
      clients:
        notification-service:
          address: localhost:7000
        chat-service:
          address: localhost:7001
```
