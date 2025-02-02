package com.luanvan.mediaservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadAvatarCommand {
    @TargetAggregateIdentifier
    private String id;
    private String userId;
    private String avatarUrl;
}
