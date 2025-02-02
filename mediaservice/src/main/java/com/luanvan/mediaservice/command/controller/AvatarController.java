package com.luanvan.mediaservice.command.controller;

import com.luanvan.mediaservice.command.command.UploadAvatarCommand;
import com.luanvan.mediaservice.services.CloudinaryService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class AvatarController {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("avatar/{userId}")
    public String uploadAvatar(@PathVariable String userId, @RequestParam("avatar") MultipartFile avatar) {
        try {
            UploadAvatarCommand command = new UploadAvatarCommand(
                    UUID.randomUUID().toString(),
                    userId,
                    cloudinaryService.uploadAvatar(avatar.getBytes(),userId)
            );
           return commandGateway.sendAndWait(command);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

}
