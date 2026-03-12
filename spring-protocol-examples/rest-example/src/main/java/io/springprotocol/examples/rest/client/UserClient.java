package io.springprotocol.examples.rest.client;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;
import io.springprotocol.examples.rest.dto.CreateUserRequest;
import io.springprotocol.examples.rest.dto.User;

@SpringClient(protocol = ProtocolType.REST, serviceId = "user-service")
public interface UserClient {

    @ProtocolMapping(value = "/users/{id}", method = "GET")
    User getUser(String id);

    @ProtocolMapping(value = "/users", method = "POST")
    User createUser(CreateUserRequest request);

    @ProtocolMapping(value = "/users/{id}", method = "DELETE")
    void deleteUser(String id);
}
