package com.luanvan.userservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarUploadedEvent {
    private String userId;
    private String avatarUrl;
}
