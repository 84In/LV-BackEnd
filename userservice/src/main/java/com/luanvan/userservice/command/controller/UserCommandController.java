package com.luanvan.userservice.command.controller;

import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.command.command.DeleteUserCommand;
import com.luanvan.userservice.command.command.RemoveUserCommand;
import com.luanvan.userservice.command.command.UpdateUserCommand;
import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.model.UserDeleteModel;
import com.luanvan.userservice.command.model.UserUpdateModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public ApiResponse<?> createUser(@RequestBody UserCreateModel model) {
        try {
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
            log.info("Send command create user: {}", command);
            var response = new HashMap<>();
            response.put("id", commandGateway.sendAndWait(command));
            return ApiResponse.builder()
                    .code(200)
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .code(404)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PutMapping("/{userId}")
    public ApiResponse<?> updateUser(@PathVariable String userId, @RequestBody UserUpdateModel model) {
        try {
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
            log.info("Send command update user: {}", command);
            var response = new HashMap<>();
            response.put("id", commandGateway.sendAndWait(command));
            return ApiResponse.builder()
                    .code(200)
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .code(404)
                    .message(e.getMessage())
                    .build();
        }
    }


    @DeleteMapping("/delete/{userId}/")
    public String deleteUser(@PathVariable String userId) {
        DeleteUserCommand command = new DeleteUserCommand(userId);
        log.info("Send command delete user: {}", command);
        return commandGateway.sendAndWait(command);


    }

    @DeleteMapping("/{userId}")
    public String removeUser(@PathVariable String userId) {
        try {
            RemoveUserCommand command = new RemoveUserCommand(userId, false);
            log.info("Send command remove user: {}", command);
            return commandGateway.sendAndWait(command);
        } catch (Exception e) {
            return e.getMessage();
        }
    }


}
