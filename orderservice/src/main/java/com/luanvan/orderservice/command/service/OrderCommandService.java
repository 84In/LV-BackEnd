package com.luanvan.orderservice.command.service;


import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.orderservice.command.command.CreateOrderCommand;
import com.luanvan.orderservice.command.model.OrderCreateModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderCommandService {

    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?, ?> createWithCash(OrderCreateModel model) {
        GetUserQuery userQuery = new GetUserQuery(model.getUsername());
        UserResponseModel user = queryGateway.query(userQuery, ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        for(OrderCreateModel.OrderDetail od : model.getOrderDetails()) {
            GetProductQuery productQuery = new GetProductQuery(od.getProductId());
            ProductResponseModel product = queryGateway.query(productQuery, ResponseTypes.instanceOf(ProductResponseModel.class))
                    .exceptionally(ex -> {throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);})
                    .join();
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
        }
        CreateOrderCommand command = CreateOrderCommand.builder()
                .id(UUID.randomUUID().toString())
                .username(model.getUsername())
                .totalPrice(model.getTotalPrice())
                .discountPrice(model.getDiscountPrice())
                .paymentMethod(model.getPaymentMethod())
                .delivery(CreateOrderCommand.Delivery.builder()
                        .id(UUID.randomUUID().toString())
                        .recipientName(model.getDelivery().getRecipientName())
                        .phone(model.getDelivery().getPhone())
                        .province(model.getDelivery().getProvince())
                        .district(model.getDelivery().getDistrict())
                        .ward(model.getDelivery().getWard())
                        .street(model.getDelivery().getStreet())
                        .address(model.getDelivery().getAddress())
                        .build())
                .orderDetails(model.getOrderDetails().stream()
                        .map(od -> CreateOrderCommand.OrderDetail.builder()
                                .id(UUID.randomUUID().toString())
                                .quantity(od.getQuantity())
                                .originalPrice(od.getOriginalPrice())
                                .purchasePrice(od.getPurchasePrice())
                                .productId(od.getProductId())
                                .colorId(od.getColorId())
                                .sizeId(od.getSizeId())
                                .isReview(false)
                                .build()).collect(Collectors.toList())
                )
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }
}
