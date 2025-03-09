package com.luanvan.orderservice.command.aggregate;

import com.luanvan.orderservice.command.command.*;
import com.luanvan.orderservice.command.event.*;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class ReviewAggregate {
    @AggregateIdentifier
    private String id;
    private Integer rating;
    private String comment;
    private String userId;
    private String productId;
    private String orderDetailId;

    @CommandHandler
    public ReviewAggregate(CreateReviewCommand command) {
        ReviewCreateEvent event = new ReviewCreateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ReviewCreateEvent event) {
        this.id = event.getId();
        this.rating = event.getRating();
        this.comment = event.getComment();
        this.userId = event.getUserId();
        this.productId = event.getProductId();
        this.orderDetailId = event.getOrderDetailId();
    }
}
