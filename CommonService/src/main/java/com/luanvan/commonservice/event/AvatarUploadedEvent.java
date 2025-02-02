package com.luanvan.commonservice.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
