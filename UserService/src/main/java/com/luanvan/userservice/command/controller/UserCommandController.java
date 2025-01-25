package com.luanvan.userservice.command.controller;

import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.dto.UserCreateModel;
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
                model.getAvatar(),
                model.getRoleName());
        log.info("Send command to {}", command);
        return commandGateway.sendAndWait(command);
    }
}
