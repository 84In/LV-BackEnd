package com.luanvan.mediaservice.command.controller;

import com.luanvan.mediaservice.command.command.UploadAvatarCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
public class AvatarController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("avatar/{userId}")
    public void uploadAvatar(@PathVariable String userId, @RequestParam("avatar") MultipartFile avatar) {
        try {
            UploadAvatarCommand command = new UploadAvatarCommand(
                    userId,
                    avatar.getBytes()
            );
            commandGateway.sendAndWait(command);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

}
