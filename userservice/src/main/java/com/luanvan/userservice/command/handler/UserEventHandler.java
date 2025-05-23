package com.luanvan.userservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.userservice.command.event.*;
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
            if (userRepository.existsByUsername(event.getUsername())) {
                log.warn("User '{}' already exists, skipping event processing", event.getUsername());
                return; // Bỏ qua sự kiện nếu user đã tồn tại
            }
            if (userRepository.existsByEmail(event.getEmail())) {
                log.warn("User '{}' already exists, skipping event processing", event.getEmail());
                return; // Bỏ qua sự kiện nếu user đã tồn tại
            }

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
            userRepository.save(user);
            log.info("User created successfully {}",user.getUsername());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        try {
            User user = userRepository.findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if(event.getEmail()!= null && !event.getEmail().trim().isEmpty()){
                if(!userRepository.existsByEmail(event.getEmail())){
                    user.setEmail(event.getEmail());
                }else{
                    if(!event.getEmail().equals(user.getEmail())){
                        user.setEmail(event.getEmail());
                    }
                }
            }

            if (event.getPhone()!= null && !event.getPhone().trim().isEmpty()) {
                user.setPhone(event.getPhone());
            }
            if (event.getFirstName()!= null && !event.getFirstName().trim().isEmpty()) {
                user.setFirstName(event.getFirstName());
            }
            if (event.getLastName()!= null && !event.getLastName().trim().isEmpty()) {
                user.setLastName(event.getLastName());
            }
            if (event.getRoleName()!= null && !event.getRoleName().trim().isEmpty()) {
                Role role = roleRepository.findById(event.getRoleName())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                user.setRole(role);
            }

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
        User user = userRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        log.info("User change status: {}",user.getUsername());
        user.setActive(event.getActive());
        log.info("User change status event handler {}",event.getId());
        userRepository.save(user);
    }

    @EventHandler
    public void on(UserChangePasswordEvent event){
        User user = userRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setPassword(passwordEncoder.encode(event.getPassword()));
        log.info("User change password event handler");
        userRepository.save(user);
    }

}
