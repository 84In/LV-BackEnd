package com.luanvan.productservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.command.*;
import com.luanvan.productservice.command.model.CategoryCreateModel;
import com.luanvan.productservice.command.model.CategoryUpdateModel;
import com.luanvan.productservice.command.model.SizeCreateModel;
import com.luanvan.productservice.command.model.SizeUpdateModel;
import com.luanvan.productservice.repository.CategoryRepository;
import com.luanvan.productservice.repository.SizeRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class SizeCommandService {
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?, ?> save(SizeCreateModel model) throws AppException {
        if (sizeRepository.existsByName(model.getName())) {
            log.info("Size already exists with name " + model.getName());
            throw new AppException(ErrorCode.SIZE_EXISTED);
        } else {
            CreateSizeCommand command = CreateSizeCommand
                    .builder()
                    .id(UUID.randomUUID().toString())
                    .name(model.getName())
                    .codeName(model.getCodeName())
                    .isActive(true)
                    .build();
            var result = new HashMap<>();
            result.put("id", commandGateway.sendAndWait(command));
            log.info("send {} ", model.getName());
            return result;
        }
    }

    public HashMap<?, ?> update(String id, SizeUpdateModel model) throws AppException {
        if (!sizeRepository.existsById(id)) {
            throw new AppException(ErrorCode.SIZE_NOT_EXISTED);
        }
        UpdateSizeCommand command = UpdateSizeCommand.builder()
                .id(id)
                .name(model.getName())
                .codeName(model.getCodeName())
                .isActive(model.getIsActive())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> delete(String id) throws AppException {
        if (!sizeRepository.existsById(id)) {
            throw new AppException(ErrorCode.SIZE_NOT_EXISTED);
        }
        DeleteSizeCommand command = DeleteSizeCommand.builder()
                .id(id)
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

}
