package com.luanvan.orderservice.command.aggregate;

import com.luanvan.orderservice.command.command.CancelOrderCommand;
import com.luanvan.orderservice.command.command.ChangePaymentStatusOrderCommand;
import com.luanvan.orderservice.command.command.ChangeStatusOrderCommand;
import com.luanvan.orderservice.command.command.CreateOrderCommand;
import com.luanvan.orderservice.command.event.OrderCancelEvent;
import com.luanvan.orderservice.command.event.OrderChangePaymentStatusEvent;
import com.luanvan.orderservice.command.event.OrderChangeStatusEvent;
import com.luanvan.orderservice.command.event.OrderCreateEvent;
import com.luanvan.commonservice.entity.PaymentStatus;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Aggregate
@NoArgsConstructor
public class OrderAggregate {
    @AggregateIdentifier
    private String id;
    private String username;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private String paymentMethod;
    private String orderStatus;
    private String transactionId;
    private PaymentStatus paymentStatus;
    private OrderCreateEvent.Payment paymentCreate;
    private OrderCreateEvent.Delivery deliveryCreate;
    private List<OrderCreateEvent.OrderDetail> orderDetailsCreate;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        OrderCreateEvent event = OrderCreateEvent.builder()
                .id(command.getId())
                .username(command.getUsername())
                .totalPrice(command.getTotalPrice())
                .discountPrice(command.getDiscountPrice())
                .paymentMethod(command.getPaymentMethod())
                .payment(command.getPayment() == null
                            ? null
                            : OrderCreateEvent.Payment.builder()
                        .id(command.getPayment().getId())
                        .totalAmount(command.getPayment().getTotalAmount())
                        .status(command.getPayment().getStatus())
                        .build()
                )
                .delivery(OrderCreateEvent.Delivery.builder()
                        .id(command.getDelivery().getId())
                        .recipientName(command.getDelivery().getRecipientName())
                        .phone(command.getDelivery().getPhone())
                        .province(command.getDelivery().getProvince())
                        .district(command.getDelivery().getDistrict())
                        .ward(command.getDelivery().getWard())
                        .street(command.getDelivery().getStreet())
                        .address(command.getDelivery().getAddress())
                        .build())
                .orderDetails(command.getOrderDetails().stream()
                        .map(od -> OrderCreateEvent.OrderDetail.builder()
                                .id(od.getId())
                                .quantity(od.getQuantity())
                                .originalPrice(od.getOriginalPrice())
                                .purchasePrice(od.getPurchasePrice())
                                .productId(od.getProductId())
                                .colorId(od.getColorId())
                                .sizeId(od.getSizeId())
                                .isReview(od.getIsReview())
                                .build()).collect(Collectors.toList())
                )
                .build();
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangeStatusOrderCommand command){
        OrderChangeStatusEvent event = new OrderChangeStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(CancelOrderCommand command){
        OrderCancelEvent event = new OrderCancelEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangePaymentStatusOrderCommand command){
        OrderChangePaymentStatusEvent event = new OrderChangePaymentStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreateEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.totalPrice = event.getTotalPrice();
        this.discountPrice = event.getDiscountPrice();
        this.paymentMethod = event.getPaymentMethod();
        this.paymentCreate = event.getPayment();
        this.deliveryCreate = event.getDelivery();
        this.orderDetailsCreate = event.getOrderDetails();
    }

    @EventSourcingHandler
    public void on(OrderChangeStatusEvent event) {
        this.id = event.getId();
        this.orderStatus = event.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderCancelEvent event) {
        this.id = event.getId();
    }

    @EventSourcingHandler
    public void on(OrderChangePaymentStatusEvent event) {
        this.id = event.getId();
        this.transactionId = event.getTransactionId();
        this.paymentStatus = event.getPaymentStatus();
    }
}
