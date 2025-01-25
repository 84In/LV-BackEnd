package com.luanvan.userservice.command.controller;

import com.luanvan.userservice.command.command.CreateAddressCommand;
import com.luanvan.userservice.dto.AddressCreateModel;
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
    private CommandGateway commandGateway;

    @PostMapping
    public String createAddress(@RequestBody AddressCreateModel model) {
        CreateAddressCommand command = new CreateAddressCommand(
                UUID.randomUUID().toString(),
                true,
                model.getPhone(),
                model.getHouseNumberAndStreet(),
                model.getProvinceId(),
                model.getDistrictId(),
                model.getWardId(),
                model.getUserId()
        );
        log.info("Created command Address: {}", command);
        return commandGateway.sendAndWait(command);
    }
}
