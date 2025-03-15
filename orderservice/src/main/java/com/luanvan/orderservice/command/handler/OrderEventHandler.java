package com.luanvan.orderservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.RollBackStockProductCommand;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.orderservice.command.event.OrderCancelEvent;
import com.luanvan.orderservice.command.event.OrderChangePaymentStatusEvent;
import com.luanvan.orderservice.command.event.OrderChangeStatusEvent;
import com.luanvan.orderservice.command.event.OrderCreateEvent;
import com.luanvan.orderservice.entity.*;
import com.luanvan.orderservice.repository.OrderDetailRepository;
import com.luanvan.orderservice.repository.OrderRepository;
import com.luanvan.orderservice.repository.OrderStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final QueryGateway queryGateway;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CommandGateway commandGateway;

    @EventHandler
    @Transactional
    public void handle(OrderCreateEvent event) {
        log.info("OrderCreateEvent: " + event.getId());

        String confirmedStatus = "confirmed";
        GetUserQuery userQuery = new GetUserQuery(event.getUsername());
        UserResponseModel user = queryGateway.query(userQuery, ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        Delivery delivery = Delivery.builder()
                .id(event.getDelivery().getId())
                .recipientName(event.getDelivery().getRecipientName())
                .phone(event.getDelivery().getPhone())
                .province(event.getDelivery().getProvince())
                .district(event.getDelivery().getDistrict())
                .ward(event.getDelivery().getWard())
                .street(event.getDelivery().getStreet())
                .address(event.getDelivery().getAddress())
                .build();
        Order order;

        if (!event.getPaymentMethod().equals("cash") && event.getPayment() != null) {
            Payment payment = Payment.builder()
                    .id(event.getPayment().getId())
                    .totalAmount(event.getPayment().getTotalAmount())
                    .status(event.getPayment().getStatus())
                    .build();
            order = Order.builder()
                    .id(event.getId())
                    .userId(user.getId())
                    .totalPrice(event.getTotalPrice())
                    .discountPrice(event.getDiscountPrice())
                    .paymentMethod(event.getPaymentMethod())
                    .payment(payment)
                    .orderStatus(orderStatusRepository.findById(confirmedStatus).get())
                    .delivery(delivery)
                    .build();
        } else {
            order = Order.builder()
                    .id(event.getId())
                    .userId(user.getId())
                    .totalPrice(event.getTotalPrice())
                    .discountPrice(event.getDiscountPrice())
                    .paymentMethod(event.getPaymentMethod())
                    .orderStatus(orderStatusRepository.findById(confirmedStatus).get())
                    .delivery(delivery)
                    .build();
        }

        var finalOrder = orderRepository.save(order);

        var oderDetailByProduct = event.getOrderDetails().stream()
                .collect(Collectors.groupingBy(OrderCreateEvent.OrderDetail::getProductId));
        var productMap = oderDetailByProduct.keySet().stream()
                .collect(Collectors.toMap(
                        pid -> pid,
                        pid -> queryGateway.query(new GetProductQuery(pid),
                                        ResponseTypes.instanceOf(ProductResponseModel.class))
                                .exceptionally(ex -> {
                                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                                })
                                .join()
                ));
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderCreateEvent.OrderDetail od : event.getOrderDetails()) {
            var product = productMap.get(od.getProductId());
            var productColor = product.getProductColors().stream()
                    .filter(pc -> pc.getColor().getId().equals(od.getColorId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COLOR_NOT_EXISTED));
            var productVariant = productColor.getProductVariants().stream()
                    .filter(pv -> pv.getSize().getId().equals(od.getSizeId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));
            if (od.getQuantity() > productVariant.getStock()) {
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            OrderDetail orderDetail = OrderDetail.builder()
                    .id(od.getId())
                    .quantity(od.getQuantity())
                    .originalPrice(od.getOriginalPrice())
                    .purchasePrice(od.getPurchasePrice())
                    .productId(od.getProductId())
                    .colorId(od.getColorId())
                    .sizeId(od.getSizeId())
                    .promotionId(od.getPromotionId())
                    .isReview(od.getIsReview())
                    .order(finalOrder)
                    .build();
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
    }

    @EventHandler
    @Transactional
    public void handle(OrderChangeStatusEvent event) {
        log.info("OrderChangeStatusEvent: " + event.getId());

        var orderStatus = orderStatusRepository.findById(event.getOrderStatus())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_ORDER_NOT_EXISTED));
        var order = orderRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    @EventHandler
    @Transactional
    public void handle(OrderCancelEvent event) {
        log.info("OrderCancelEvent: " + event.getId());
        String cancelledStatus = "cancelled";

        // Chuyển order thành cancelled
        var orderStatus = orderStatusRepository.findById(cancelledStatus)
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_ORDER_NOT_EXISTED));
        var order = orderRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        // Rollback tất cả stock cho sản phẩm
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            RollBackStockProductCommand rollBackCmd = RollBackStockProductCommand.builder()
                    .id(orderDetail.getProductId())
                    .quantity(orderDetail.getQuantity())
                    .colorId(orderDetail.getColorId())
                    .sizeId(orderDetail.getSizeId())
                    .build();
            commandGateway.sendAndWait(rollBackCmd);
        }
    }

    @EventHandler
    @Transactional
    public void handle(OrderChangePaymentStatusEvent event) {
        log.info("OrderChangePaymentStatusEvent: " + event.getId());

        var order = orderRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        order.getPayment().setStatus(event.getPaymentStatus());
        if(event.getTransactionId() != null && !event.getTransactionId().isEmpty()) {
            order.getPayment().setTransactionId(event.getTransactionId());
        }
        orderRepository.save(order);
    }
}
