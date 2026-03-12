package io.springprotocol.examples.rest.service;

import io.springprotocol.examples.rest.client.UserClient;
import io.springprotocol.examples.rest.dto.CreateUserRequest;
import io.springprotocol.examples.rest.dto.User;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserClient userClient;

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public User findUser(String id) {
        return userClient.getUser(id);
    }

    public User registerUser(String name, String email) {
        CreateUserRequest request = new CreateUserRequest(name, email);
        return userClient.createUser(request);
    }

    public void removeUser(String id) {
        userClient.deleteUser(id);
    }
}
