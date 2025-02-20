package com.luanvan.userservice.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.userservice.command.command.ChangeStatusUserCommand;
import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.command.command.DeleteUserCommand;
import com.luanvan.userservice.command.command.UpdateUserCommand;
import com.luanvan.userservice.command.model.UserChangeStatusModel;
import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.model.UserUpdateModel;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class UserCommandService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?,?> save(UserCreateModel model) throws AppException {
        if(userRepository.existsByUsername(model.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if(!roleRepository.existsById(model.getRoleName())){
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }

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
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?,?> update(String userId, UserUpdateModel model) throws AppException {

        if(!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if(model.getRoleName() != null && !model.getRoleName().isEmpty() && !roleRepository.existsById(model.getRoleName())) {
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }

        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                model.getPassword(),
                model.getEmail(),
                model.getPhone(),
                model.getLastName(),
                model.getFirstName(),
                model.getRoleName()
        );
        log.info("Send command update user: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }
    
    public HashMap<?,?> delete(String userId) throws AppException {
        DeleteUserCommand command = new DeleteUserCommand(userId);
        log.info("Send command delete user: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        result.put("message", "Người dùng đã bị vô hiệu hoá");
        return result;
    }

    public HashMap<?,?> changeStatus(String userId, UserChangeStatusModel model) throws AppException {
        if(!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        ChangeStatusUserCommand command = new ChangeStatusUserCommand(userId,model.getActive());
        log.info("Send command change status user: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        result.put("message","Trạng thái tài khoản đã được cập nhật");
        return result;
    }
}
