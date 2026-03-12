package io.springprotocol.examples.graphql.client;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;

@SpringClient(protocol = ProtocolType.GRAPHQL, serviceId = "user-service")
public interface UserGraphqlClient {

    // @ProtocolMapping(query = "{ user(id: $id) { name email } }")
    // UserDto getUser(String id);

    // @ProtocolMapping(query = "mutation { createUser(name: $name) { id name } }", operationType = "MUTATION")
    // UserDto createUser(String name);
}
