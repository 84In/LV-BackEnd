package com.luanvan.productservice.command.aggregate;

import com.luanvan.commonservice.event.ColorChangeStatusEvent;
import com.luanvan.productservice.command.command.*;
import com.luanvan.commonservice.event.ColorUpdateEvent;
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
public class ColorAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String codeName;
    private String colorCode;
    private String description;
    private Boolean isActive;

    @CommandHandler
    public ColorAggregate(CreateColorCommand command) {
        ColorCreateEvent event = new ColorCreateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateColorCommand command) {
        ColorUpdateEvent event = new ColorUpdateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangeStatusColorCommand command) {
        ColorChangeStatusEvent event = new ColorChangeStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteColorCommand command) {
        ColorDeleteEvent event = new ColorDeleteEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ColorCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.colorCode = event.getColorCode();
        this.description = event.getDescription();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(ColorUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.colorCode = event.getColorCode();
        this.description = event.getDescription();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(ColorChangeStatusEvent event) {
        this.id = event.getId();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(ColorDeleteEvent event) {
        this.id = event.getId();
    }
}
