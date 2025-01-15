package com.luanvan.userservice.command.controller;

import com.luanvan.userservice.command.command.CreateProvinceCommand;

import com.luanvan.userservice.dto.ProvinceRequestModel;
import com.luanvan.userservice.dto.ProvinceResponseModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/provinces")
public class ProvinceCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<ProvinceResponseModel> addProvince(@RequestBody ProvinceRequestModel model) {
        CreateProvinceCommand command = new CreateProvinceCommand(
                model.getCode(),
                model.getName(),
                model.getCodeName(),
                model.getDivisionType(),
                true
        );

        String provinceId = commandGateway.sendAndWait(command);  // Đảm bảo bạn có CommandGateway trong service.

        // Lấy thông tin phản hồi từ Command handler
        ProvinceResponseModel provinceResponseModel = new ProvinceResponseModel();

        BeanUtils.copyProperties(model, provinceResponseModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(provinceResponseModel);
    }
}
