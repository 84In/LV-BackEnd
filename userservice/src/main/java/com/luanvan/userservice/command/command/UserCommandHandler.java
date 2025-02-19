package com.luanvan.userservice.command.command;


import com.luanvan.userservice.command.event.UserCreatedEvent;
import com.luanvan.userservice.command.event.UserDeletedEvent;
import com.luanvan.userservice.command.event.UserRemoveEvent;
import com.luanvan.userservice.command.event.UserUpdatedEvent;
import com.luanvan.userservice.entity.Role;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserCommandHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @CommandHandler
    public void handle(CreateUserCommand command) {
        UserCreatedEvent event = new UserCreatedEvent();

        if(userRepository.existsByUsername(command.getUsername())){
           throw new RuntimeException("Người dùng đẫ tồn tại!");
        }
        Role role = roleRepository.findById(command.getRoleName()).orElseThrow(()-> new RuntimeException("Vai trò không tồn tại!"));

        BeanUtils.copyProperties(command, event);
        log.info("UserAggregate - UserCreatedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateUserCommand command) {
        UserUpdatedEvent event = new UserUpdatedEvent();

        if(!userRepository.existsById(command.getId())){
            throw new RuntimeException("Người dùng chưa tồn tại!");
        }
        if(command.getRoleName() != null){
            if (!roleRepository.existsById(command.getRoleName())) {
                throw new RuntimeException("Vai trò không tồn tại!");
            }
        }
        BeanUtils.copyProperties(command, event);
        log.info("UserUpdatedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteUserCommand command) {
        UserDeletedEvent event = new UserDeletedEvent();
        event.setId(command.getId());
        log.info("UserDeletedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void hanle(RemoveUserCommand command) {
        UserRemoveEvent event = new UserRemoveEvent();
        event.setId(command.getId());
        event.setActive(command.getActive());
        log.info("UserRemovedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }
}
