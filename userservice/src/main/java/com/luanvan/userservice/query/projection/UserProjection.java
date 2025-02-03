package com.luanvan.userservice.query.projection;

import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.query.model.UserResponseModel;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserProjection {

    @Autowired
    private UserRepository userRepository;

    @QueryHandler
    public List<UserResponseModel> handle(GetAllUserQuery query){
        log.info("handle get all users");
        List<User> users = userRepository.findAll();
        log.info(users.toString());
        log.info("handle get users success");
        return users.stream().map(user -> {
            UserResponseModel userResponseModel = new UserResponseModel();
            userResponseModel.setId(user.getId());
            userResponseModel.setUsername(user.getUsername());
            userResponseModel.setPassword(user.getPassword());
            userResponseModel.setEmail(user.getEmail());
            userResponseModel.setPhone(user.getPhone());
            userResponseModel.setFirstName(user.getFirstName());
            userResponseModel.setLastName(user.getLastName());
            userResponseModel.setAvatar(user.getAvatar());
            userResponseModel.setActive(user.getActive());
            userResponseModel.setRole(user.getRole());
            userResponseModel.setAddresses(user.getAddresses());
            userResponseModel.setCreatedAt(user.getCreatedAt());
            userResponseModel.setUpdatedAt(user.getUpdatedAt());
            log.info("handle get all users dto");
            log.info(userResponseModel.toString());
            return userResponseModel;
        }).toList();
    }
}
