package com.luanvan.productservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.command.ChangeStatusCategoryCommand;
import com.luanvan.productservice.command.command.CreateCategoryCommand;
import com.luanvan.productservice.command.command.DeleteCategoryCommand;
import com.luanvan.productservice.command.command.UpdateCategoryCommand;
import com.luanvan.productservice.command.model.CategoryChangeStatusModel;
import com.luanvan.productservice.command.model.CategoryCreateModel;
import com.luanvan.productservice.command.model.CategoryUpdateModel;
import com.luanvan.productservice.repository.CategoryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class CategoryCommandService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?, ?> save(CategoryCreateModel model) throws AppException {
        if (categoryRepository.existsByName(model.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        } else {
            CreateCategoryCommand command = CreateCategoryCommand
                    .builder()
                    .id(UUID.randomUUID().toString())
                    .name(model.getName())
                    .codeName(model.getCodeName())
                    .description(model.getDescription())
                    .images(model.getImages())
                    .isActive(true)
                    .build();
            var result = new HashMap<>();
            result.put("id", commandGateway.sendAndWait(command));
            return result;
        }
    }

    public HashMap<?, ?> update(String categoryId, CategoryUpdateModel model) throws AppException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                .id(categoryId)
                .name(model.getName())
                .codeName(model.getCodeName())
                .description(model.getDescription())
                .images(model.getImages())
                .isActive(model.getIsActive())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> changeStatus(String categoryId, CategoryChangeStatusModel model) throws AppException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        ChangeStatusCategoryCommand command =  ChangeStatusCategoryCommand.builder()
                .id(categoryId)
                .isActive(model.getIsActive())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> delete(String categoryId) throws AppException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                .id(categoryId)
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

}
