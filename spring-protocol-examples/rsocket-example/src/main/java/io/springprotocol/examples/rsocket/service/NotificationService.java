package io.springprotocol.examples.rsocket.service;

import io.springprotocol.examples.rsocket.client.NotificationClient;
import io.springprotocol.examples.rsocket.dto.NotificationRequest;
import io.springprotocol.examples.rsocket.dto.NotificationResponse;
import io.springprotocol.examples.rsocket.dto.Score;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Service;

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
