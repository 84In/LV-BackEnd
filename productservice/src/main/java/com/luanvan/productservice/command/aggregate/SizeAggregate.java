package com.luanvan.productservice.command.aggregate;

import com.luanvan.commonservice.event.SizeChangeStatusEvent;
import com.luanvan.commonservice.event.SizeUpdateEvent;
import com.luanvan.productservice.command.command.*;
import com.luanvan.productservice.command.event.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Slf4j
@Aggregate
@NoArgsConstructor
public class SizeAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String codeName;
    private Boolean isActive;

    @CommandHandler
    public SizeAggregate(CreateSizeCommand command) {
        SizeCreateEvent event = new SizeCreateEvent();
        BeanUtils.copyProperties(command, event);
        log.info("Create size: {}", event.toString());
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateSizeCommand command) {
        SizeUpdateEvent event = new SizeUpdateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangeStatusSizeCommand command) {
        SizeChangeStatusEvent event = new SizeChangeStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteSizeCommand command) {
        SizeDeleteEvent event = new SizeDeleteEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(SizeCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(SizeUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(SizeChangeStatusEvent event) {
        this.id = event.getId();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(SizeDeleteEvent event) {
        this.id = event.getId();
    }
}
