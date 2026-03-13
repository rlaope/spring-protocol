package io.springprotocol.examples.rest.runner;

import io.springprotocol.examples.rest.dto.User;
import io.springprotocol.examples.rest.service.UserService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RestExampleRunner implements CommandLineRunner {

    private final UserService userService;

    public RestExampleRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Spring Protocol REST Example ===");

        System.out.println("[1] Creating user...");
        User created = userService.registerUser("Hope Kim", "hope@example.com");
        System.out.println("    Created: id=" + created.getId() + ", name=" + created.getName() + ", email=" + created.getEmail());

        System.out.println("[2] Getting user...");
        User found = userService.findUser(created.getId());
        System.out.println("    Found: id=" + found.getId() + ", name=" + found.getName() + ", email=" + found.getEmail());

        System.out.println("[3] Deleting user...");
        userService.removeUser(created.getId());
        System.out.println("    Deleted.");

        System.out.println("=== REST Example Complete ===");
    }
}
