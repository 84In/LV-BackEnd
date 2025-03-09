package com.luanvan.orderservice.command.command;

import com.luanvan.commonservice.entity.PaymentStatus;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePaymentStatusOrderCommand {
    @TargetAggregateIdentifier
    private String id;
    private String transactionId;
    private PaymentStatus paymentStatus;
}
