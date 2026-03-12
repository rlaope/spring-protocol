package io.springprotocol.examples.rsocket.client;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;
import io.springprotocol.examples.rsocket.dto.NotificationRequest;
import io.springprotocol.examples.rsocket.dto.NotificationResponse;
import io.springprotocol.examples.rsocket.dto.Score;
import reactor.core.publisher.Flux;

@SpringClient(protocol = ProtocolType.RSOCKET, serviceId = "notification-service")
public interface NotificationClient {

    @ProtocolMapping(route = "notify", interaction = "REQUEST_RESPONSE")
    NotificationResponse sendNotification(NotificationRequest request);

    @ProtocolMapping(route = "notify.fire", interaction = "FIRE_AND_FORGET")
    void fireNotification(NotificationRequest request);

    @ProtocolMapping(route = "scores.stream", interaction = "REQUEST_STREAM")
    Flux<Score> streamScores(String matchId);
}
