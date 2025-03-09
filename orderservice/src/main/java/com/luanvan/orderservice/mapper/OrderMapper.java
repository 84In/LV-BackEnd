package com.luanvan.orderservice.mapper;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.entity.OrderDetail;
import com.luanvan.orderservice.query.model.OrderDetailResponse;
import com.luanvan.orderservice.query.model.OrderResponseModel;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    @Autowired
    private QueryGateway queryGateway;

    public OrderResponseModel toOrderResponseModel(Order order) {
        var cartDetailsByProduct = order.getOrderDetails().stream()
                .collect(Collectors.groupingBy(OrderDetail::getProductId));

        // Truy vấn sản phẩm cho mỗi productId một lần và lưu vào map
        var productMap = cartDetailsByProduct.keySet().stream()
                .collect(Collectors.toMap(
                        pid -> pid,
                        pid -> queryGateway.query(new GetProductQuery(pid),
                                        ResponseTypes.instanceOf(ProductResponseModel.class))
                                .exceptionally(ex -> {
                                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                                })
                                .join()
                ));
        return OrderResponseModel.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .paymentMethod(order.getPaymentMethod())
                .userId(order.getUserId())
                .orderDetails(order.getOrderDetails().stream()
                        .map(od -> mapOrderDetail(od, productMap.get(od.getProductId())))
                        .collect(Collectors.toList()))
                .orderStatus(OrderResponseModel.OrderStatus.builder()
                        .codeName(order.getOrderStatus().getCodeName())
                        .name(order.getOrderStatus().getName())
                        .build())
                .delivery(OrderResponseModel.Delivery.builder()
                        .id(order.getDelivery().getId())
                        .recipientName(order.getDelivery().getRecipientName())
                        .phone(order.getDelivery().getPhone())
                        .province(order.getDelivery().getProvince())
                        .district(order.getDelivery().getDistrict())
                        .ward(order.getDelivery().getWard())
                        .street(order.getDelivery().getStreet())
                        .address(order.getDelivery().getAddress())
                        .startDate(order.getDelivery().getStartDate())
                        .endDate(order.getDelivery().getEndDate())
                        .build())
                .payment(order.getPayment() == null
                        ? null
                        : OrderResponseModel.Payment.builder()
                        .id(order.getPayment().getId())
                        .transactionId(order.getPayment().getTransactionId())
                        .totalAmount(order.getPayment().getTotalAmount())
                        .status(order.getPayment().getStatus())
                        .createdAt(order.getPayment().getCreatedAt())
                        .updatedAt(order.getPayment().getUpdatedAt())
                        .build())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderDetailResponse mapOrderDetail(OrderDetail od, ProductResponseModel product) {
        // Lấy productColor theo colorId
        var productColor = product.getProductColors().stream()
                .filter(pc -> pc.getColor().getId().equals(od.getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COLOR_NOT_EXISTED));

        var colorResponse = OrderDetailResponse.Color.builder()
                .id(productColor.getColor().getId())
                .name(productColor.getColor().getName())
                .codeName(productColor.getColor().getCodeName())
                .colorCode(productColor.getColor().getColorCode())
                .description(productColor.getColor().getDescription())
                .isActive(productColor.getColor().getIsActive())
                .build();

        // Lấy productVariant theo sizeId
        var productVariant = productColor.getProductVariants().stream()
                .filter(pv -> pv.getSize().getId().equals(od.getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));

        var sizeResponse = OrderDetailResponse.Size.builder()
                .id(productVariant.getSize().getId())
                .name(productVariant.getSize().getName())
                .codeName(productVariant.getSize().getCodeName())
                .isActive(productVariant.getSize().getIsActive())
                .build();

        return OrderDetailResponse.builder()
                .id(od.getId())
                .quantity(od.getQuantity())
                .originalPrice(od.getOriginalPrice())
                .purchasePrice(od.getPurchasePrice())
                .isReview(od.getIsReview())
                .color(colorResponse)
                .size(sizeResponse)
                .product(toProductResponseModel(product))
                .build();
    }

    public OrderDetailResponse.Product toProductResponseModel(ProductResponseModel product) {
        return OrderDetailResponse.Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(product.getImages())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .category(OrderDetailResponse.Category.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .codeName(product.getCategory().getCodeName())
                        .images(product.getCategory().getImages())
                        .description(product.getCategory().getDescription())
                        .isActive(product.getCategory().getIsActive())
                        .build())
                .productColors(product.getProductColors().stream()
                        .filter(pc -> Boolean.TRUE.equals(pc.getIsActive()))
                        .map(pc -> OrderDetailResponse.ProductColor.builder()
                                .id(pc.getId())
                                .price(pc.getPrice())
                                .finalPrice(pc.getFinalPrice())
                                .isActive(pc.getIsActive())
                                .color(OrderDetailResponse.Color.builder()
                                        .id(pc.getColor().getId())
                                        .name(pc.getColor().getName())
                                        .codeName(pc.getColor().getCodeName())
                                        .colorCode(pc.getColor().getColorCode())
                                        .description(pc.getColor().getDescription())
                                        .isActive(pc.getColor().getIsActive())
                                        .build())
                                .promotion(Optional.ofNullable(pc.getPromotion())
                                        .map(promo -> OrderDetailResponse.Promotion.builder()
                                                .id(promo.getId())
                                                .name(promo.getName())
                                                .codeName(promo.getCodeName())
                                                .discountPercentage(promo.getDiscountPercentage())
                                                .startDate(promo.getStartDate())
                                                .endDate(promo.getEndDate())
                                                .isActive(promo.getIsActive())
                                                .build())
                                        .orElse(null))
                                .productVariants(pc.getProductVariants().stream()
                                        .map(pv -> OrderDetailResponse.ProductVariant.builder()
                                                .id(pv.getId())
                                                .stock(pv.getStock())
                                                .sold(pv.getSold())
                                                .isActive(pv.getIsActive())
                                                .size(OrderDetailResponse.Size.builder()
                                                        .id(pv.getSize().getId())
                                                        .name(pv.getSize().getName())
                                                        .codeName(pv.getSize().getCodeName())
                                                        .isActive(pv.getSize().getIsActive())
                                                        .build())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
