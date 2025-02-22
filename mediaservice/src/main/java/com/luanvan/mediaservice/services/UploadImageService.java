package com.luanvan.mediaservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.command.UploadProductImagesCommand;
import com.luanvan.commonservice.model.ProductImagesUploadModel;
import com.luanvan.commonservice.services.KafkaService;
import com.luanvan.commonservice.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UploadImageService {
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private KafkaTemplate<String, ProductImagesUploadModel> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "upload-product-images", groupId = "product-group")
    public void handle(String message) {
        try {
            UploadProductImagesCommand command = objectMapper.readValue(message, UploadProductImagesCommand.class);
            ArrayList<String> imageUrls = (ArrayList<String>) command.getImages().stream()
                    .map(image -> cloudinaryService.uploadFile(ImageUtils.decodeImageFromBase64(image), "products/" + command.getProductId()))
                    .toList();

            log.info("Upload products images successful productId: {}", command.getProductId());
            var event = new ProductImagesUploadModel(command.getProductId(), imageUrls);
            kafkaTemplate.send("product-images-uploaded-topic", event);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
