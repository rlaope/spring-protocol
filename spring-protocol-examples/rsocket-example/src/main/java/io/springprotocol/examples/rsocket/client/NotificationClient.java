package io.springprotocol.examples.rsocket.client;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;

@SpringClient(protocol = ProtocolType.RSOCKET, serviceId = "notification-service")
public interface NotificationClient {

    // @ProtocolMapping(route = "notify", interaction = "FIRE_AND_FORGET")
    // void sendNotification(NotificationRequest request);

    // @ProtocolMapping(route = "scores.stream", interaction = "REQUEST_STREAM")
    // Flux<Score> streamScores(String matchId);
}
