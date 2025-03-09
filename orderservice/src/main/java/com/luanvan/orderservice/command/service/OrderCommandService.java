package com.luanvan.orderservice.command.service;


import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.SendConfirmedOrderMailCommand;
import com.luanvan.commonservice.entity.PaymentStatus;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.commonservice.utils.VNPayUtils;
import com.luanvan.orderservice.command.command.CancelOrderCommand;
import com.luanvan.orderservice.command.command.ChangePaymentStatusOrderCommand;
import com.luanvan.orderservice.command.command.ChangeStatusOrderCommand;
import com.luanvan.orderservice.command.command.CreateOrderCommand;
import com.luanvan.orderservice.command.model.OrderChangeStatusModel;
import com.luanvan.orderservice.command.model.OrderCreateModel;
import com.luanvan.orderservice.command.model.PaymentUrlResponse;
import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.repository.OrderRepository;
import com.luanvan.orderservice.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderCommandService {
    @Value("${client.url}")
    @NonFinal
    String clientUrl;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private KafkaTemplate<String, SendConfirmedOrderMailCommand> kafkaTemplate;

    public HashMap<?, ?> createWithCash(OrderCreateModel model) {
        log.info("Create order command service with cash for username: {}", model.getUsername());
        var result = createOrder(model);
        return result;
    }

    public PaymentUrlResponse createWithVNPay(HttpServletRequest request, OrderCreateModel model) {
        log.info("Create order VNPay command service with cash for username: {}", model.getUsername());
        var result = createOrder(model);
        log.info(String.valueOf(result.get("id")));
        long amount = Long.parseLong(String.valueOf(model.getTotalPrice())) * 100L;
        // String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayService.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnpParamsMap.put("vnp_TxnRef", String.valueOf(result.get("id")));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toán đơn hàng:" + result.get("id"));
        vnpParamsMap.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));
        // Build query url
        String queryUrl = vnPayService.generateUrl(vnpParamsMap);
        String paymentUrl = vnPayService.getVnp_PayUrl() + "?" + queryUrl;

        return PaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    public void vnPayCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("VNPay callback");
        String vnpResponseCode = request.getParameter("vnp_ResponseCode");
        String vnpTxnRef = request.getParameter("vnp_TxnRef");
        String vnpTransactionNo = request.getParameter("vnp_TransactionNo");
        Order order = orderRepository.findById(vnpTxnRef)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String redirectUrl;

        if ("00".equals(vnpResponseCode)) {
            ChangePaymentStatusOrderCommand changePaymentStatusOrderCommand = ChangePaymentStatusOrderCommand.builder()
                    .id(order.getId())
                    .transactionId(vnpTransactionNo)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .build();
            commandGateway.sendAndWait(changePaymentStatusOrderCommand);
            redirectUrl = clientUrl + "/checkout-result?status=successful&orderId=" + order.getId();
            response.sendRedirect(redirectUrl);
        } else {
            String cancelledStatus = "cancelled";
            ChangeStatusOrderCommand changeStatusOrderCommand = ChangeStatusOrderCommand.builder()
                    .id(order.getId())
                    .orderStatus(cancelledStatus)
                    .build();
            ChangePaymentStatusOrderCommand changePaymentStatusOrderCommand = ChangePaymentStatusOrderCommand.builder()
                    .id(order.getId())
                    .paymentStatus(PaymentStatus.FAILED)
                    .build();
            commandGateway.sendAndWait(changeStatusOrderCommand);
            commandGateway.sendAndWait(changePaymentStatusOrderCommand);
            redirectUrl = clientUrl + "/checkout-result?status=failure&orderId=" + order.getId();
            response.sendRedirect(redirectUrl);
        }
    }

    public HashMap<?, ?> changeOrderStatus(OrderChangeStatusModel model) {
        ChangeStatusOrderCommand command = ChangeStatusOrderCommand.builder()
                .id(model.getId())
                .orderStatus(model.getOrderStatus())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> cancelOrder(HttpServletRequest request, String orderId) {
        String cancelledStatus = "cancelled";
        String completedStatus = "completed";

        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        if (order.getOrderStatus().getCodeName().equals(cancelledStatus) || order.getOrderStatus().getCodeName().equals(completedStatus)) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELED);
        }

        // Refund Payment
        if (order.getPayment() != null && order.getPayment().getStatus().equals(PaymentStatus.SUCCESS)) {
            if (order.getPaymentMethod().equals("vnpay")) {
                Boolean response = vnPayService.refundVNPay(request, order);
                if (response != Boolean.TRUE) {
                    throw new AppException(ErrorCode.PAYMENT_CANNOT_REFUND_VNPAY);
                }
            }
        }

        CancelOrderCommand command = new CancelOrderCommand(orderId);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    private HashMap<?, ?> createOrder(OrderCreateModel model) {
        log.info("Create order for username: {}", model.getUsername());
        GetUserQuery userQuery = new GetUserQuery(model.getUsername());
        UserResponseModel user = queryGateway.query(userQuery, ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        for (OrderCreateModel.OrderDetail od : model.getOrderDetails()) {
            GetProductQuery productQuery = new GetProductQuery(od.getProductId());
            ProductResponseModel product = queryGateway.query(productQuery, ResponseTypes.instanceOf(ProductResponseModel.class))
                    .exceptionally(ex -> {
                        throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                    })
                    .join();
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
        if (!model.getPaymentMethod().equals("cash")) {
            command.setPayment(CreateOrderCommand.Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .totalAmount(model.getTotalPrice())
                    .status(PaymentStatus.PENDING)
                    .build());
        }
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));

        // Gửi mail cho người dùng khi tạo đơn hàng thành công
        if (result.get("id") != null) {
            log.info("Send confirmed order mail for username: {}", model.getUsername());
            var sendMailCommand = SendConfirmedOrderMailCommand.builder()
                    .username(model.getUsername())
                    .orderId(String.valueOf(result.get("id")))
                    .build();
            kafkaTemplate.send("send-confirmed-order-mail-topic", sendMailCommand);
        }
        return result;
    }
}
