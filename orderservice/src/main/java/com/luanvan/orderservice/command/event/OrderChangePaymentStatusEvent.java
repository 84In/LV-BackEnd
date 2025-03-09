package com.luanvan.orderservice.command.event;

import com.luanvan.commonservice.entity.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderChangePaymentStatusEvent {
    private String id;
    private String transactionId;
    private PaymentStatus paymentStatus;
}
