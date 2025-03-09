package com.luanvan.userservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.userservice.command.model.UserChangePasswordModel;
import com.luanvan.userservice.command.model.UserChangeStatusModel;
import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.model.UserUpdateModel;
import com.luanvan.userservice.command.service.UserCommandService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserCommandController {

    @Autowired
    private UserCommandService userCommandService;

    @PostMapping
    public ApiResponse<?> createUser(@Valid @RequestBody UserCreateModel model) {

        var response = userCommandService.save(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<?> updateUser(@PathVariable String userId, @RequestBody UserUpdateModel model) {

        var response = userCommandService.update(userId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }


    @DeleteMapping("/{userId}/")
    public ApiResponse<?> deleteUser(@PathVariable String userId) {
        var response = userCommandService.delete(userId);
        return ApiResponse.builder()
                .message("Người dùng đã bị vô hiệu hoá")
                .data(response)
                .build();
    }

    @PutMapping("/changeStatus/{userId}")
    public ApiResponse<?> changeStatusUser(@PathVariable String userId, @RequestBody UserChangeStatusModel model) {
        var response = userCommandService.changeStatus(userId, model);
        return ApiResponse.builder()
                .message("Người dùng đã bị vô hiệu hoá")
                .data(response)
                .build();

    }

    @PutMapping("/changePassword/{userId}")
    public ApiResponse<?> changePasswordUser(@PathVariable String userId, @Valid @RequestBody UserChangePasswordModel model) {
        var response = userCommandService.changePassword(userId, model);
        return ApiResponse.builder()
                .message("Mật khẩu đã được cập nhật")
                .data(response)
                .build();
    }


}
