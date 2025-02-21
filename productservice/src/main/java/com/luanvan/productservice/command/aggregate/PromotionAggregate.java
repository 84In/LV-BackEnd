package com.luanvan.productservice.command.aggregate;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Aggregate
@NoArgsConstructor
public class PromotionAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String codeName;
    private String description;
    private Double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;

    @CommandHandler
    public PromotionAggregate(CreatePromotionCommand command) {
        PromotionCreateEvent event = new PromotionCreateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdatePromotionCommand command) {
        PromotionUpdateEvent event = new PromotionUpdateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeletePromotionCommand command) {
        PromotionDeleteEvent event = new PromotionDeleteEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PromotionCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.description = event.getDescription();
        this.discountPercentage = event.getDiscountPercentage();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(PromotionUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.description = event.getDescription();
        this.discountPercentage = event.getDiscountPercentage();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(PromotionDeleteEvent event) {
        this.id = event.getId();
    }
}
