package com.luanvan.mediaservice.command.controller;

import com.luanvan.commonservice.model.AvatarUpdateModel;
import com.luanvan.commonservice.model.CategoryImageUpdateModel;
import com.luanvan.commonservice.model.ProductImagesUploadModel;
import com.luanvan.commonservice.services.KafkaService;
import com.luanvan.commonservice.utils.ImageUtils;
import com.luanvan.mediaservice.services.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {
    private final CloudinaryService cloudinaryService;

    private final KafkaService kafkaService;

    @PostMapping("avatar/{userId}")
    public ResponseEntity<?> uploadAvatar(@PathVariable String userId, @RequestParam("avatar") MultipartFile avatar) {

        log.info("Saved uploadAvatar: {}", userId);
        AvatarUpdateModel avatarUpdateModel = new AvatarUpdateModel();

        avatarUpdateModel.setUserId(userId);
        avatarUpdateModel.setAvatarUrl(cloudinaryService.uploadAvatar(avatar, userId));

        log.info("Send message to kafka topic avatar-uploaded-topic with user-id {}", userId);

        kafkaService.sendMessage("avatar-uploaded-topic", avatarUpdateModel);


        return ResponseEntity.status(HttpStatus.OK).body("Cập nhật hình ảnh thành công!");
    }

    @PostMapping("categories/{categoryId}")
    public ResponseEntity<?> uploadCategoriesImage(@PathVariable String categoryId, @RequestParam("images") MultipartFile images) {

        log.info("Saved uploadCategoriesImage: {}", categoryId);
        CategoryImageUpdateModel categoryImageUpdateModel = new CategoryImageUpdateModel();

        categoryImageUpdateModel.setCategoryId(categoryId);
        categoryImageUpdateModel.setCategoryUrl(cloudinaryService.uploadCategoryImage(images, categoryId));

        log.info("Send message to kafka topic category-uploaded-topic with categoryId {}", categoryId);

        kafkaService.sendMessage("category-image-uploaded-topic", categoryImageUpdateModel);

        return ResponseEntity.status(HttpStatus.OK).body("Cập nhật hình ảnh thành công!");
    }

    @PostMapping("product/{productId}")
    public ResponseEntity<?> uploadProductImages(@PathVariable String productId, @RequestParam("images") ArrayList<MultipartFile> images) {

        log.info("Saved uploadProductImage: {}", productId);
        List<String> listImageUrls = images.stream()
                .map(image -> cloudinaryService.uploadFile(image, "products/" + productId))
                .toList();
        ArrayList<String> imageUrls = new ArrayList<>(listImageUrls);
        log.info("Upload products images successful productId: {}", productId);
        var event = new ProductImagesUploadModel(productId, imageUrls);
        kafkaService.sendMessage("product-images-uploaded-topic", event);

        return ResponseEntity.status(HttpStatus.OK).body("Cập nhật hình ảnh thành công!");
    }
}
