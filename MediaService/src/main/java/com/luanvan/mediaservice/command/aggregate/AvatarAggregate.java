package com.luanvan.mediaservice.command.aggregate;

import com.luanvan.commonservice.event.AvatarUploadedEvent;
import com.luanvan.mediaservice.command.command.UploadAvatarCommand;
import com.luanvan.mediaservice.services.CloudinaryService;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

@Aggregate
@NoArgsConstructor
public class AvatarAggregate {
    @AggregateIdentifier
    private String userId;

    private String avatarUrl;

    @Autowired
    private CloudinaryService cloudinaryService;

    @CommandHandler
    public AvatarAggregate(UploadAvatarCommand command) {
        AvatarUploadedEvent event = new AvatarUploadedEvent();
        event.setUserId(command.getUserId());
        event.setAvatarUrl(cloudinaryService.uploadAvatar(command.getAvatar(), command.getUserId()));

        AggregateLifecycle.apply(event);
    }


    @EventSourcingHandler
    public void onAvatarUploaded(AvatarUploadedEvent event) {
        this.userId = event.getUserId();
        this.avatarUrl = event.getAvatarUrl();
    }


}
