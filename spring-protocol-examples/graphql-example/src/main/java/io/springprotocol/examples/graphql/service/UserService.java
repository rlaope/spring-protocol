package io.springprotocol.examples.graphql.service;

import io.springprotocol.examples.graphql.client.UserGraphqlClient;
import io.springprotocol.examples.graphql.dto.UserDto;

import org.springframework.stereotype.Service;

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
