package com.luanvan.orderservice.command.aggregate;

import com.luanvan.orderservice.command.command.CreateOrderCommand;
import com.luanvan.orderservice.command.event.OrderCreateEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Aggregate
public class OrderAggregate {
    @AggregateIdentifier
    private String id;
    private String username;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private String paymentMethod;
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

    @EventSourcingHandler
    public void on(OrderCreateEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.totalPrice = event.getTotalPrice();
        this.discountPrice = event.getDiscountPrice();
        this.paymentMethod = event.getPaymentMethod();
        this.deliveryCreate = event.getDelivery();
        this.orderDetailsCreate = event.getOrderDetails();
    }
}
