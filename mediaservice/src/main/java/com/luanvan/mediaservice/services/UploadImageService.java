package com.luanvan.mediaservice.services;

import com.luanvan.commonservice.command.UploadProductImagesCommand;
import com.luanvan.commonservice.command.CallBackUploadProductImagesCommand;
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
    private KafkaTemplate<String, CallBackUploadProductImagesCommand> kafkaTemplate;

    @KafkaListener(topics = "upload-product-images", groupId = "product-group")
    public void handle(UploadProductImagesCommand message) {
        log.info("Received upload product images command for productId: {}", message.getProductId());
        try {
            List<String> imageUrls = message.getImages().stream()
                    .map(image -> cloudinaryService.uploadFile(
                            ImageUtils.decodeImageFromBase64(image),
                            "products/" + message.getProductId()))
                    .toList();

            log.info("Upload products images successful for productId: {}", message.getProductId());
            CallBackUploadProductImagesCommand event = new CallBackUploadProductImagesCommand(message.getProductId(), new ArrayList<>(imageUrls));
            kafkaTemplate.send("product-images-uploaded-topic", event);
        } catch (Exception e) {
            log.error("Error handling upload product images command: {}", e.getMessage(), e);
        }
    }
}
