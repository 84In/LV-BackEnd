package com.luanvan.userservice.command.aggregate;

import com.luanvan.userservice.command.command.*;
import com.luanvan.userservice.command.event.*;
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
public class CartAggregate {
    @AggregateIdentifier
    private String id;
    private String username;
    private CartCreatedEvent.CartDetail cartDetailCreate;
    private CartAddToEvent.CartDetail cartDetailAddTo;
    private CartUpdatedEvent.CartDetail cartDetailUpdate;
    private String cartDetailId;

    @CommandHandler
    public CartAggregate(CreateEmptyCartCommand command) {
        CartEmptyCreatedEvent event = CartEmptyCreatedEvent.builder()
                .id(command.getId())
                .username(command.getUsername())
                .build();
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(CreateCartCommand command) {
        CartCreatedEvent event = CartCreatedEvent.builder()
                .id(command.getId())
                .username(command.getUsername())
                .cartDetail(CartCreatedEvent.CartDetail.builder()
                        .id(command.getCartDetail().getId())
                        .quantity(command.getCartDetail().getQuantity())
                        .productId(command.getCartDetail().getProductId())
                        .colorId(command.getCartDetail().getColorId())
                        .sizeId(command.getCartDetail().getSizeId())
                        .build())
                .build();
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(AddToCartCommand command) {
        CartAddToEvent event = CartAddToEvent.builder()
                .id(command.getId())
                .username(command.getUsername())
                .cartDetail(CartAddToEvent.CartDetail.builder()
                        .id(command.getCartDetail().getId())
                        .quantity(command.getCartDetail().getQuantity())
                        .productId(command.getCartDetail().getProductId())
                        .colorId(command.getCartDetail().getColorId())
                        .sizeId(command.getCartDetail().getSizeId())
                        .build())
                .build();
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateCartCommand command) {
        CartUpdatedEvent event = CartUpdatedEvent.builder()
                .id(command.getId())
                .username(command.getUsername())
                .cartDetail(CartUpdatedEvent.CartDetail.builder()
                        .id(command.getCartDetail().getId())
                        .quantity(command.getCartDetail().getQuantity())
                        .productId(command.getCartDetail().getProductId())
                        .colorId(command.getCartDetail().getColorId())
                        .sizeId(command.getCartDetail().getSizeId())
                        .build())
                .build();
        AggregateLifecycle.apply(event);
    }
    
    @CommandHandler
    public void handle(DeleteCartCommand command){
        CartDeletedEvent event = new CartDeletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteAllCartCommand command){
        CartDeletedAllEvent event = new CartDeletedAllEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CartEmptyCreatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
    }

    @EventSourcingHandler
    public void on(CartCreatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.cartDetailCreate = event.getCartDetail();
    }

    @EventSourcingHandler
    public void on(CartAddToEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.cartDetailAddTo = event.getCartDetail();
    }

    @EventSourcingHandler
    public void on(CartUpdatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.cartDetailUpdate = event.getCartDetail();
    }

    @EventSourcingHandler
    public void on(CartDeletedEvent event) {
        this.id = event.getId();
        this.cartDetailId = event.getCartDetailId();
    }

    @EventSourcingHandler
    public void on(CartDeletedAllEvent event) {
        this.id = event.getId();
    }
}
