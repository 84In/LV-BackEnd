package com.luanvan.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.model.CategoryImageUpdateModel;
import com.luanvan.productservice.entity.Category;
import com.luanvan.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadImageService {

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "category-uploaded-topic", groupId = "category-group")
    public void on(String message) {

        System.out.println(message);

        try {
            CategoryImageUpdateModel model = objectMapper.readValue(message, CategoryImageUpdateModel.class);
            Category category = categoryRepository.findById(model.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
            category.setImages(model.getCategoryUrl());
            categoryRepository.save(category);
            log.info("Category URL updated for categoryId: {}", model.getCategoryId());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
