package com.luanvan.userservice.command.handler;

import com.luanvan.userservice.command.event.UserChangeStatusEvent;
import com.luanvan.userservice.command.event.UserCreatedEvent;
import com.luanvan.userservice.command.event.UserDeletedEvent;
import com.luanvan.userservice.command.event.UserUpdatedEvent;
import com.luanvan.userservice.entity.Cart;
import com.luanvan.userservice.entity.Role;
import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.repository.CartRepository;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class UserEventHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CartRepository cartRepository;

    @EventHandler
    public void on(UserCreatedEvent event) {
        try {
            log.info("User created user event handler");

            Role role = roleRepository.findByName(event.getRoleName());
            log.info("User created role event handler");
            User user = new User();
            user.setId(event.getId());
            user.setUsername(event.getUsername());
            user.setPassword(passwordEncoder.encode(event.getPassword()));
            user.setEmail(event.getEmail());
            user.setPhone(event.getPhone());
            user.setFirstName(event.getFirstName());
            user.setLastName(event.getLastName());
            user.setActive(event.getActive());
            user.setRole(role);
            var finalUser = userRepository.save(user);

            var cart = cartRepository.findByUser(finalUser)
                    .orElseGet(() -> Cart.builder()
                            .id(UUID.randomUUID().toString())
                            .user(finalUser)
                            .cartDetails(new ArrayList<>())
                            .build());
            cartRepository.save(cart);
            log.info("User created successfully");
        }catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        try {
            User user = userRepository.findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional.ofNullable(event.getEmail()).ifPresent(user::setEmail);
            Optional.ofNullable(event.getPhone()).ifPresent(user::setPhone);
            Optional.ofNullable(event.getFirstName()).ifPresent(user::setFirstName);
            Optional.ofNullable(event.getLastName()).ifPresent(user::setLastName);

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
    public void on(UserChangeStatusEvent event) {
        User user = userRepository.findByUserId(event.getId());
        user.setActive(event.getActive());
        userRepository.save(user);
    }

}
