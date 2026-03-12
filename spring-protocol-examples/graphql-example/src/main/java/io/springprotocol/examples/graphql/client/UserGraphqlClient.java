package io.springprotocol.examples.graphql.client;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;
import io.springprotocol.examples.graphql.dto.UserDto;

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
