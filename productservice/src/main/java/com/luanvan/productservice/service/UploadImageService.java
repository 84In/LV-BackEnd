package com.luanvan.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.ProductImagesUploadModel;
import com.luanvan.commonservice.model.CategoryImageUpdateModel;
import com.luanvan.productservice.entity.Category;
import com.luanvan.productservice.entity.Product;
import com.luanvan.productservice.repository.CategoryRepository;
import com.luanvan.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadImageService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "category-image-uploaded-topic", groupId = "category-group")
    public void uploadedCategoryImage(String message) {

        System.out.println(message);

        try {
            CategoryImageUpdateModel model = objectMapper.readValue(message, CategoryImageUpdateModel.class);
            Category category = categoryRepository.findById(model.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
            category.setImages(model.getCategoryUrl());
            categoryRepository.save(category);
            log.info("Category URL updated for categoryId: {}", model.getCategoryId());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }

    @KafkaListener(topics = "product-images-uploaded-topic", groupId = "product-group")
    public void uploadedProductImages(String message) {

        log.info(message);

        try {
            ProductImagesUploadModel event = objectMapper.readValue(message, ProductImagesUploadModel.class);
            Product product = productRepository.findById(event.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            product.setImages(event.getImageUrls().toString());
            productRepository.save(product);
            log.info("Product images URL updated for categoryId: {}", event.getProductId());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
