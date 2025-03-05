package com.luanvan.orderservice.command.event;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelEvent {
    private String id;
}
