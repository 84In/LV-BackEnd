package com.luanvan.mediaservice.command.aggregate;

import com.luanvan.commonservice.event.AvatarUploadedEvent;
import com.luanvan.mediaservice.command.command.UploadAvatarCommand;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class AvatarAggregate {
    @AggregateIdentifier
    private String id;
    private String userId;
    private String avatarUrl;


    @CommandHandler
    public AvatarAggregate(UploadAvatarCommand command) {
        AvatarUploadedEvent event = new AvatarUploadedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }


    @EventSourcingHandler
    public void onAvatarUploaded(AvatarUploadedEvent event) {
        this.id = event.getId();
        this.userId = event.getUserId();
        this.avatarUrl = event.getAvatarUrl();
    }


}
