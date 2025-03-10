package com.luanvan.productservice.command.aggregate;

import com.luanvan.productservice.command.command.ChangeStatusCategoryCommand;
import com.luanvan.productservice.command.command.CreateCategoryCommand;
import com.luanvan.productservice.command.command.DeleteCategoryCommand;
import com.luanvan.productservice.command.command.UpdateCategoryCommand;
import com.luanvan.productservice.command.event.CategoryChangeStatusEvent;
import com.luanvan.productservice.command.event.CategoryCreateEvent;
import com.luanvan.productservice.command.event.CategoryDeleteEvent;
import com.luanvan.productservice.command.event.CategoryUpdateEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Slf4j
@Aggregate
@NoArgsConstructor
public class CategoryAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String codeName;
    private String description;
    private String images;
    private Boolean isActive;

    @CommandHandler
    public CategoryAggregate(CreateCategoryCommand command) {
        CategoryCreateEvent event = new CategoryCreateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateCategoryCommand command) {
        CategoryUpdateEvent event = new CategoryUpdateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangeStatusCategoryCommand command) {
        CategoryChangeStatusEvent event = new CategoryChangeStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteCategoryCommand command) {
        CategoryDeleteEvent event = new CategoryDeleteEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CategoryCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.description = event.getDescription();
        this.images = event.getImages();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(CategoryUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.description = event.getDescription();
        this.images = event.getImages();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(CategoryChangeStatusEvent event) {
        this.id = event.getId();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(CategoryDeleteEvent event) {
        this.id = event.getId();
    }
}
