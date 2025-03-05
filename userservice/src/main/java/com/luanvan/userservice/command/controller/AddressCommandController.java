package com.luanvan.userservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.userservice.command.command.CreateAddressCommand;
import com.luanvan.userservice.command.event.AddressChangeDefaultEvent;
import com.luanvan.userservice.command.model.AddressChangeDefaultModel;
import com.luanvan.userservice.command.model.AddressCreateModel;
import com.luanvan.userservice.command.model.AddressRemoveModel;
import com.luanvan.userservice.command.model.AddressUpdateModel;
import com.luanvan.userservice.command.service.AddressCommandService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressCommandController {
    @Autowired
    AddressCommandService addressCommandService;

    @PostMapping
    public ApiResponse<?> createAddress(@RequestBody AddressCreateModel model) {
        var response = addressCommandService.save(model);
        return ApiResponse.builder()
                .message("Địa chỉ đã được lưu!")
                .data(response)
                .build();
    }

    @PutMapping("/{addressId}")
    public ApiResponse<?> updateAddress(@PathVariable String addressId, @RequestBody AddressUpdateModel model) {
        var response = addressCommandService.update(model, addressId);
        return ApiResponse.builder()
                .message("Địa chỉ đã được cập nhật!")
                .data(response)
                .build();
    }

    @PutMapping("/changeDefault/{addressId}")
    public ApiResponse<?> changeDefault(@PathVariable String addressId, @RequestBody AddressChangeDefaultModel model) {
        var response = addressCommandService.changeDefault(addressId, model);
        return ApiResponse.builder()
                .message("Cập nhật địa chỉ mặc định thành công!")
                .data(response)
                .build();
    }

    @DeleteMapping("/remove/{addressId}")
    public ApiResponse<?> removeAddress(@PathVariable String addressId, @RequestBody AddressRemoveModel model) {
        var response = addressCommandService.remove(addressId, model);
        return ApiResponse.builder()
                .message("Cập nhật đã được xoá!")
                .data(response)
                .build();
    }


}
