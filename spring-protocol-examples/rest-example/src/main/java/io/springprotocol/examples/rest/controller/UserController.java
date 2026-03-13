package io.springprotocol.examples.rest.controller;

import io.springprotocol.examples.rest.dto.CreateUserRequest;
import io.springprotocol.examples.rest.dto.User;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class UserController {

    private final Map<String, User> store = new ConcurrentHashMap<>();

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        User user = store.get(id);
        if (user == null) {
            throw new RuntimeException("User not found: " + id);
        }
        return user;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody CreateUserRequest request) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        User user = new User(id, request.getName(), request.getEmail());
        store.put(id, user);
        return user;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable String id) {
        store.remove(id);
    }
}
