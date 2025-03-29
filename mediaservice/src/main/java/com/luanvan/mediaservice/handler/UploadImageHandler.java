package com.luanvan.mediaservice.handler;

import com.luanvan.commonservice.event.ProductUploadImagesEvent;
import com.luanvan.commonservice.command.CallBackUploadProductImagesCommand;
import com.luanvan.commonservice.utils.ImageUtils;
import com.luanvan.mediaservice.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UploadImageHandler{
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private KafkaTemplate<String, CallBackUploadProductImagesCommand> kafkaTemplate;
    @Autowired
    private CommandGateway commandGateway;

    @EventHandler
    public void handle(ProductUploadImagesEvent message) {
        log.info("Received upload product images command for productId: {}", message.getProductId());
        try {
            List<String> imageUrls = message.getImages().stream()
                    .map(image -> cloudinaryService.uploadFile(
                            ImageUtils.decodeImageFromBase64(image),
                            "products/" + message.getProductId()))
                    .toList();

            log.info("Upload products images successful for productId: {}", message.getProductId());
            CallBackUploadProductImagesCommand command = new CallBackUploadProductImagesCommand(message.getProductId(), new ArrayList<>(imageUrls));
            commandGateway.send(command);
//            kafkaTemplate.send("product-images-uploaded-topic", command);
        } catch (Exception e) {
            log.error("Error handling upload product images command: {}", e.getMessage(), e);
        }
    }
}