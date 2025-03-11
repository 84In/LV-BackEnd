package com.luanvan.mediaservice.command.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.request.AvatarUpdateModel;
import com.luanvan.commonservice.model.request.CategoryImageUpdateModel;
import com.luanvan.commonservice.model.request.ProductImagesUploadModel;
import com.luanvan.commonservice.services.KafkaService;
import com.luanvan.mediaservice.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, ProductImagesUploadModel> kafkaTemplate;

    @PostMapping("avatar/{userId}")
    public ApiResponse<?> uploadAvatar(@PathVariable String userId, @RequestParam("avatar") MultipartFile avatar) throws AppException {

        try {
            log.info("Saved uploadAvatar: {}", userId);
            AvatarUpdateModel avatarUpdateModel = new AvatarUpdateModel();

            avatarUpdateModel.setUserId(userId);
            avatarUpdateModel.setAvatarUrl(cloudinaryService.uploadAvatar(avatar, userId));

            log.info("Send message to kafka topic avatar-uploaded-topic with user-id {}", userId);

            kafkaService.sendMessage("avatar-uploaded-topic", avatarUpdateModel);
            return ApiResponse.builder()
                    .message("Cập nhật hình ảnh thành công!")
                    .build();
        }catch (Exception e) {
            log.error("Upload avatar failed for user {}: {}", userId, e.getMessage(), e);
            // Trả về thông báo lỗi cho người dùng ngay lập tức
            throw new AppException(ErrorCode.FILE_ERROR);
        }


    }

    @PostMapping("categories/{categoryId}")
    public ApiResponse<?> uploadCategoriesImage(@PathVariable String categoryId, @RequestParam("images") MultipartFile images) throws AppException {

        try{
            log.info("Saved uploadCategoriesImage: {}", categoryId);
            CategoryImageUpdateModel categoryImageUpdateModel = new CategoryImageUpdateModel();

            categoryImageUpdateModel.setCategoryId(categoryId);
            categoryImageUpdateModel.setCategoryUrl(cloudinaryService.uploadCategoryImage(images, categoryId));

            log.info("Send message to kafka topic category-uploaded-topic with categoryId {}", categoryId);

            kafkaService.sendMessage("category-image-uploaded-topic", categoryImageUpdateModel);

            return ApiResponse.builder()
                    .message("Cập nhật hình ảnh thành công!")
                    .build();
        }catch (Exception e) {
            log.error("Upload category failed for user {}: {}", categoryId, e.getMessage(), e);
            // Trả về thông báo lỗi cho người dùng ngay lập tức
            throw new AppException(ErrorCode.FILE_ERROR);
        }
    }

    @PostMapping("products/{productId}")
    public ApiResponse<?> uploadProductImages(@PathVariable String productId, @RequestParam("images") ArrayList<MultipartFile> images) {

        log.info("Saved uploadProductImage: {}", productId);
        List<String> imageUrls = images.stream()
                .map(image -> cloudinaryService.uploadFile(image, "products/" + productId))
                .toList();
        log.info("Upload products images successful productId: {}", productId);
        return ApiResponse.builder()
                .data(String.join(",", imageUrls))
                .build();
    }
}
