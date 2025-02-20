package com.luanvan.productservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.command.CreateCategoryCommand;
import com.luanvan.productservice.command.command.DeleteCategoryCommand;
import com.luanvan.productservice.command.command.UpdateCategoryCommand;
import com.luanvan.productservice.command.model.CategoryCreateModel;
import com.luanvan.productservice.command.model.CategoryUpdateModel;
import com.luanvan.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class CategoryCommandService {
    @Autowired
    private  CategoryRepository categoryRepository;
    @Autowired
    private  CommandGateway commandGateway;

    public HashMap<?, ?> saveCategory(CategoryCreateModel model) throws AppException {
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

    public HashMap<?, ?> updateCategory(String categoryId, CategoryUpdateModel model) throws AppException {
        if(!categoryRepository.existsById(categoryId)) {
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

    public HashMap<?, ?> deleteCategory(String categoryId) throws AppException {
        if(!categoryRepository.existsById(categoryId)) {
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
