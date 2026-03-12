# RSocket Client - Quick Start

## 1. Add Dependency

```gradle
dependencies {
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rsocket-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 2. Configure Target Address

```yaml
spring:
  protocol:
    rsocket:
      clients:
        notification-service:
          address: localhost:7000
```

## 3. Define DTOs

```java
public class NotificationRequest {
    private String userId;
    private String message;
    // constructors, getters, setters
}

public class NotificationResponse {
    private String status;
    private String notificationId;
    // constructors, getters, setters
}

public class Score {
    private String matchId;
    private String team;
    private int points;
    // constructors, getters, setters
}
```

## 4. Declare Client Interface

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

- `protocol`: Set to `ProtocolType.RSOCKET`.
- `serviceId`: Maps to `spring.protocol.rsocket.clients.{serviceId}.address`.
- `@ProtocolMapping.route`: The RSocket route string.
- `@ProtocolMapping.interaction`: The interaction model.

### Interaction Models

| Interaction | Return Type | Description |
|-------------|------------|-------------|
| `REQUEST_RESPONSE` | `T` | Send one message, receive one response. Default if omitted. |
| `FIRE_AND_FORGET` | `void` | Send one message, no response expected. |
| `REQUEST_STREAM` | `Flux<T>` | Send one message, receive a stream of responses. |

### Payload Serialization

- The first method argument is serialized to JSON and sent as the data payload.
- The route string is sent as metadata.
- Responses are deserialized from JSON using Jackson.

## 5. Enable Scanning

```java
@SpringBootApplication
@EnableSpringClients(basePackages = "io.springprotocol.examples.rsocket.client")
public class RSocketExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RSocketExampleApplication.class, args);
    }
}
```

## 6. Inject and Use

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

## Implementation Details

- Uses `rsocket-java` with TCP transport (`rsocket-transport-netty`).
- Connections are established lazily and cached per address — one TCP connection per service.
- JSON serialization/deserialization powered by Jackson `ObjectMapper`.
- `REQUEST_RESPONSE` calls `RSocket.requestResponse()` and blocks for the result.
- `FIRE_AND_FORGET` calls `RSocket.fireAndForget()` — returns immediately.
- `REQUEST_STREAM` calls `RSocket.requestStream()` — returns a reactive `Flux<T>`.
- Connections are disposed on application shutdown via `ProtocolClientHandler.destroy()`.

## Error Handling

Connection failures and serialization errors throw `RSocketClientException`:

```java
try {
    NotificationResponse response = notificationClient.sendNotification(request);
} catch (RSocketClientException e) {
    System.out.println(e.getMessage());
}
```

## Multiple Services

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
