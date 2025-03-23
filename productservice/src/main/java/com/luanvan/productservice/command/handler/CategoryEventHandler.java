package com.luanvan.productservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.event.CategoryChangeStatusEvent;
import com.luanvan.productservice.command.event.CategoryCreateEvent;
import com.luanvan.productservice.command.event.CategoryDeleteEvent;
import com.luanvan.commonservice.event.CategoryUpdateEvent;
import com.luanvan.productservice.entity.Category;
import com.luanvan.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryEventHandler {
    private final CategoryRepository categoryRepository;

    @EventHandler
    public void on(CategoryCreateEvent event) throws Exception {
        log.info("Category created");
        var category = Category.builder()
                .id(event.getId())
                .name(event.getName())
                .codeName(event.getCodeName())
                .description(event.getDescription())
                .images(event.getImages())
                .isActive(event.getIsActive())
                .build();
        categoryRepository.save(category);
    }

    @EventHandler
    public void on(CategoryUpdateEvent event) {
        log.info("Category updated");
        var category = categoryRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        category.setName(event.getName());
        category.setCodeName(event.getCodeName());
        category.setDescription(event.getDescription());
        category.setImages(event.getImages());
        category.setIsActive(event.getIsActive());
        categoryRepository.save(category);
    }

    @EventHandler
    public void on(CategoryChangeStatusEvent event) {
        log.info("Category changed status");
        var category = categoryRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        category.setIsActive(event.getIsActive());
        categoryRepository.save(category);
    }

    @EventHandler
    @DisallowReplay
    public void on(CategoryDeleteEvent event) {
        log.info("Category deleted");
        var category = categoryRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        categoryRepository.delete(category);
    }
}
