package com.luanvan.mediaservice.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.model.AvatarUpdateModel;
import com.luanvan.commonservice.services.KafkaService;
import com.luanvan.mediaservice.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
public class AvatarController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private KafkaService kafkaService;

    @PostMapping("avatar/{userId}")
    public ResponseEntity uploadAvatar(@PathVariable String userId, @RequestParam("avatar") MultipartFile avatar) {

        log.info("Đã vào được: ", userId);
        AvatarUpdateModel avatarUpdateModel = new AvatarUpdateModel();

        avatarUpdateModel.setUserId(userId);
        avatarUpdateModel.setAvatarUrl(cloudinaryService.uploadAvatar(avatar, userId));

        log.info("Send message to kafka topic avatar-uploaded-topic with user-id {}", userId);

        kafkaService.sendMessage("avatar-uploaded-topic", avatarUpdateModel);


        return ResponseEntity.status(HttpStatus.OK).body("Cập nhật hình ảnh thành công!");
    }

}
