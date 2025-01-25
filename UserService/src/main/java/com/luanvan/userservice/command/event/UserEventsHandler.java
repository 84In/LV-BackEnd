package com.luanvan.userservice.command.event;

import com.luanvan.userservice.command.data.Role;
import com.luanvan.userservice.command.data.User;
import com.luanvan.userservice.command.data.repository.RoleRepository;
import com.luanvan.userservice.command.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
                    user.setAvatar(event.getAvatar());
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
}
