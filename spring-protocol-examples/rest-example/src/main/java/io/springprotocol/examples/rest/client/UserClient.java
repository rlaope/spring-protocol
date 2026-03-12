package io.springprotocol.examples.rest.client;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;

@SpringClient(protocol = ProtocolType.REST, serviceId = "user-service")
public interface UserClient {

    // @ProtocolMapping(value = "/users/{id}", method = "GET")
    // User getUser(String id);  // Uncomment when REST implementation is complete
}
