package com.luanvan.userservice.command.event;

import com.luanvan.commonservice.event.AvatarUploadedEvent;
import com.luanvan.userservice.command.data.Role;
import com.luanvan.userservice.command.data.User;
import com.luanvan.userservice.command.data.repository.RoleRepository;
import com.luanvan.userservice.command.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class UserEventsHandler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @EventHandler
    public void on(UserCreatedEvent event) {
        try {
            if(!userRepository.existsByUsername(event.getUsername())){

                Optional<Role> roles = roleRepository.findById(event.getRoleName());
                if(roles.isPresent()){
                    log.info(roles.toString());
                    User user = new User();
                    user.setId(event.getId());
                    user.setUsername(event.getUsername());
                    user.setPassword(event.getPassword());
                    user.setEmail(event.getEmail());
                    user.setPhone(event.getPhone());
                    user.setFirstName(event.getFirstName());
                    user.setLastName(event.getLastName());
                    user.setActive(event.getActive());
                    user.setRole(roles.get());
                    userRepository.save(user);
                    log.info("User created successfully");
                }else {
                    throw new Exception("Role not found");
                }

            }else {
                throw new Exception("User already exists");
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        try {
            User user = userRepository.findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional.ofNullable(event.getUsername()).ifPresent(username -> {
                if (!userRepository.existsByUsername(username)) {
                    user.setUsername(username);
                } else {
                    throw new RuntimeException("Username already exists");
                }
            });

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

    @KafkaListener(topics = "avatar-uploaded-topic", groupId = "user-group", containerFactory = "userGroupKafkaListenerContainerFactory")
    public void on(AvatarUploadedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(event.getAvatarUrl());
        userRepository.save(user);
        log.info("Avatar URL updated for User ID: {}", event.getUserId());
    }

}
