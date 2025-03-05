package com.luanvan.userservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.userservice.command.command.*;
import com.luanvan.userservice.command.model.UserChangePasswordModel;
import com.luanvan.userservice.command.model.UserChangeStatusModel;
import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.model.UserUpdateModel;
import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class UserCommandService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?,?> save(UserCreateModel model) throws AppException {
        if(userRepository.existsByUsername(model.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if(userRepository.existsByEmail(model.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
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

        CreateEmptyCartCommand cartCommand = CreateEmptyCartCommand.builder()
                .id(UUID.randomUUID().toString())
                .username(model.getUsername())
                .build();
        log.info("Send command create user: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        commandGateway.sendAndWait(cartCommand);
        return result;
    }

    public HashMap<?,?> update(String userId, UserUpdateModel model) throws AppException {

        if(!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!model.getUsername().isEmpty() && userRepository.existsByUsername(model.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if(!model.getEmail().isEmpty() && userRepository.existsByEmail(model.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if(!model.getRoleName().isEmpty() && !roleRepository.existsById(model.getRoleName())) {
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }


        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                model.getUsername(),
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

    public HashMap<?,?> changePassword(String userId, UserChangePasswordModel model) throws AppException {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!passwordEncoder.matches(model.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        ChangePasswordUserCommand command = new ChangePasswordUserCommand(userId,model.getNewPassword());
        log.info("Send command change password user: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        result.put("message","Mật khẩu đã được thay đổi");
        return result;
    }
}
