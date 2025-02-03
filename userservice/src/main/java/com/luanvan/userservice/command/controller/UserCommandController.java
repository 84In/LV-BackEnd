package com.luanvan.userservice.command.controller;

import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.command.command.UpdateUserCommand;
import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.model.UserUpdateModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String createUser(@RequestBody UserCreateModel model) {
        CreateUserCommand command = new CreateUserCommand(
                UUID.randomUUID().toString(),
                model.getUsername(),
                model.getPassword(),
                true,
                model.getEmail(),
                model.getPhone(),
                model.getLastName(),
                model.getFirstName(),
                model.getRoleName());
        log.info("Send command to {}", command);
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable String userId, @RequestBody UserUpdateModel model) {
        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                model.getUsername(),
                model.getPassword(),
                model.getActive(),
                model.getEmail(),
                model.getPhone(),
                model.getLastName(),
                model.getFirstName(),
                model.getRoleName()
        );
        log.info("Send command to {}", command);
        return commandGateway.sendAndWait(command);
    }
}
