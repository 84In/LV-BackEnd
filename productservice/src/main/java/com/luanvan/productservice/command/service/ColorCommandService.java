package com.luanvan.productservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.command.*;
import com.luanvan.productservice.command.model.ColorCreateModel;
import com.luanvan.productservice.command.model.ColorUpdateModel;
import com.luanvan.productservice.command.model.PromotionCreateModel;
import com.luanvan.productservice.command.model.PromotionUpdateModel;
import com.luanvan.productservice.repository.ColorRepository;
import com.luanvan.productservice.repository.PromotionRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class ColorCommandService {
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?, ?> save(ColorCreateModel model) throws AppException {
        if (colorRepository.existsByName(model.getName())) {
            throw new AppException(ErrorCode.COLOR_EXISTED);
        } else {
            CreateColorCommand command = CreateColorCommand
                    .builder()
                    .id(UUID.randomUUID().toString())
                    .name(model.getName())
                    .codeName(model.getCodeName())
                    .colorCode(model.getColorCode())
                    .description(model.getDescription())
                    .isActive(true)
                    .build();
            var result = new HashMap<>();
            result.put("id", commandGateway.sendAndWait(command));
            return result;
        }
    }

    public HashMap<?, ?> update(String id, ColorUpdateModel model) throws AppException {
        if (!colorRepository.existsById(id)) {
            throw new AppException(ErrorCode.COLOR_NOT_EXISTED);
        }
        UpdateColorCommand command = UpdateColorCommand.builder()
                .id(id)
                .name(model.getName())
                .codeName(model.getCodeName())
                .description(model.getDescription())
                .colorCode(model.getColorCode())
                .description(model.getDescription())
                .isActive(model.getIsActive())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> delete(String id) throws AppException {
        if (!colorRepository.existsById(id)) {
            throw new AppException(ErrorCode.COLOR_NOT_EXISTED);
        }
        DeleteColorCommand command = DeleteColorCommand.builder()
                .id(id)
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

}
