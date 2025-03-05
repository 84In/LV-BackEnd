package com.luanvan.orderservice.command.event;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderChangeStatusEvent {
    private String id;
    private String orderStatus;
}
