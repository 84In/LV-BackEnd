package com.luanvan.productservice.command.controller;

import com.luanvan.productservice.command.command.CreateCategoryCommand;
import com.luanvan.productservice.command.command.DeleteCategoryCommand;
import com.luanvan.productservice.command.command.UpdateCategoryCommand;
import com.luanvan.productservice.command.model.CategoryCreateModel;
import com.luanvan.productservice.command.model.CategoryUpdateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryCommandController {
    private final CommandGateway commandGateway;

    @PostMapping
    public String saveCategory(@RequestBody CategoryCreateModel model) {
        CreateCategoryCommand command = CreateCategoryCommand
                .builder()
                .id(UUID.randomUUID().toString())
                .name(model.getName())
                .codeName(model.getCodeName())
                .description(model.getDescription())
                .images(model.getImages())
                .isActive(true)
                .build();
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{categoryId}")
    public String updateCategory(@PathVariable String categoryId, @RequestBody CategoryUpdateModel model) {
        UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                .id(categoryId)
                .name(model.getName())
                .codeName(model.getCodeName())
                .description(model.getDescription())
                .images(model.getImages())
                .isActive(model.getIsActive())
                .build();
        return commandGateway.sendAndWait(command);
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable String categoryId) {
        DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                .id(categoryId)
                .build();
        return commandGateway.sendAndWait(command);
    }

}
