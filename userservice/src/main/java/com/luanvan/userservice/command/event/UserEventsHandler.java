package com.luanvan.userservice.command.event;

import com.luanvan.userservice.entity.Role;
import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventsHandler {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @EventHandler
    public void on(UserCreatedEvent event) throws Exception {
        log.info("User created user event handler");

        Role role = roleRepository.findById(event.getRoleName()).orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setId(event.getId());
        user.setUsername(event.getUsername());
        user.setPassword(event.getPassword());
        user.setEmail(event.getEmail());
        user.setPhone(event.getPhone());
        user.setFirstName(event.getFirstName());
        user.setLastName(event.getLastName());
        user.setActive(event.getActive());
        user.setRole(role);
        userRepository.save(user);
        log.info("User created successfully");
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        try {
            User user = userRepository.findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional.ofNullable(event.getPassword()).ifPresent(user::setPassword);
            Optional.ofNullable(event.getEmail()).ifPresent(user::setEmail);
            Optional.ofNullable(event.getPhone()).ifPresent(user::setPhone);
            Optional.ofNullable(event.getFirstName()).ifPresent(user::setFirstName);
            Optional.ofNullable(event.getLastName()).ifPresent(user::setLastName);
            Optional.ofNullable(event.getActive()).ifPresent(user::setActive);

            Optional.ofNullable(event.getRoleName()).ifPresent(roleName -> {
                Role role = roleRepository.findById(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                user.setRole(role);
            });

            userRepository.save(user);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @EventHandler
    public void on(UserDeletedEvent event) {
        userRepository.deleteById(event.getId());
    }

    @EventHandler
    public void on(UserRemoveEvent event) {
        User user = userRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(event.getId());
    }

}
