package com.luanvan.orderservice.command.event;

import com.luanvan.orderservice.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateEvent {
    private String id;
    private Integer rating;
    private String comment;
    private String userId;
    private String orderDetailId;
    private String productId;
}
