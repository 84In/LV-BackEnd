package com.luanvan.commonservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarUploadedEvent {
    private String id;
    private String userId;
    private String avatarUrl;
}
