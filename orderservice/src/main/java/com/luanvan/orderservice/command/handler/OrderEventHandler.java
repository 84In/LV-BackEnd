package com.luanvan.orderservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.UpdateStockProductCommand;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.orderservice.command.event.OrderCreateEvent;
import com.luanvan.orderservice.entity.Delivery;
import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.entity.OrderDetail;
import com.luanvan.orderservice.repository.OrderDetailRepository;
import com.luanvan.orderservice.repository.OrderRepository;
import com.luanvan.orderservice.repository.OrderStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final QueryGateway queryGateway;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @EventHandler
    @Transactional
    public void handle(OrderCreateEvent event){
        String pendingStatus = "PENDING";

        log.info("OrderCreateEvent: " + event.getId());
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
        Order order = Order.builder()
                .id(event.getId())
                .userId(user.getId())
                .totalPrice(event.getTotalPrice())
                .discountPrice(event.getDiscountPrice())
                .orderStatus(orderStatusRepository.findByCodeName(pendingStatus))
                .delivery(delivery)
                .build();
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
        for(OrderCreateEvent.OrderDetail od : event.getOrderDetails()) {
            var product = productMap.get(od.getProductId());
            var productColor = product.getProductColors().stream()
                    .filter(pc -> pc.getColor().getId().equals(od.getColorId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COLOR_NOT_EXISTED));
            var productVariant = productColor.getProductVariants().stream()
                    .filter(pv -> pv.getSize().getId().equals(od.getSizeId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));
            if(od.getQuantity() > productVariant.getStock()){
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
                    .isReview(od.getIsReview())
                    .order(finalOrder)
                    .build();
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
    }
}
