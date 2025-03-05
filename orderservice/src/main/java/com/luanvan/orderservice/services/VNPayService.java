package com.luanvan.orderservice.services;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetUserDetailQuery;
import com.luanvan.commonservice.utils.VNPayUtils;
import com.luanvan.orderservice.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Getter
@Service
public class VNPayService {
    @Autowired
    private QueryGateway queryGateway;
    @Value("${payment.vnpay.create.endpoint}")
    private String vnp_PayUrl;
    @Value("${payment.vnpay.returnUrl}")
    private String vnp_ReturnUrl;
    @Value("${payment.vnpay.tmnCode}")
    private String vnp_TmnCode;
    @Value("${payment.vnpay.hashSecret}")
    private String secretKey;
    @Value("${payment.vnpay.version}")
    private String vnp_Version;
    @Value("${payment.vnpay.orderType}")
    private String orderType;
    @Value("${payment.vnpay.query.endpoint}")
    private String queryEndpoint;
    @Value("${payment.vnpay.refund.endpoint}")
    private String refundEndpoint;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", "pay");
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }

    public Map<String, String> getQueryVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", "querydr");
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        return vnpParamsMap;
    }

    public Map<String, String> getRefundVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", "refund");
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_TransactionType", "02");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        return vnpParamsMap;
    }

    public String generateUrl(Map<String, String> paramsMap) {
        String queryUrl = VNPayUtils.getPaymentURL(paramsMap, true);
        String hashData = VNPayUtils.getPaymentURL(paramsMap, false);
        String vnpSecureHash = VNPayUtils.hmacSHA512(secretKey, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        return queryUrl;
    }

    public Map<String, String> queryVNPay(HttpServletRequest request, Order order) {
        Map<String, String> vnpParamsMap = getQueryVNPayConfig();
//        vnpParamsMap.put("vnp_RequestId", UUID.randomUUID().toString());
        vnpParamsMap.put("vnp_TxnRef", order.getId());
        vnpParamsMap.put("vnp_OrderInfo", "Truy vấn đơn hàng: " + order.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // Chuyển LocalDateTime sang ZonedDateTime với múi giờ GMT+7
        ZonedDateTime zdt = order.getPayment().getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("GMT+7"));
        String vnpTransactionDate = dtf.format(zdt);
        vnpParamsMap.put("vnp_TransactionDate", vnpTransactionDate);
        vnpParamsMap.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));

        // Tạo checksum từ dữ liệu gửi đi
        String data = VNPayUtils.buildQueryData(vnpParamsMap);
        String checksum = VNPayUtils.hmacSHA512(secretKey, data);
        vnpParamsMap.put("vnp_SecureHash", checksum);

        try {
            // Gửi POST request đến VNPAY
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(queryEndpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(vnpParamsMap)))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Parse JSON response
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            Map<String, String> responseMap = new HashMap<>();
            jsonResponse.entrySet().forEach(entry -> {
                if (entry.getValue() != null && !entry.getValue().isJsonNull()) {
                    responseMap.put(entry.getKey(), entry.getValue().getAsString());
                } else {
                    responseMap.put(entry.getKey(), "");
                }
            });
            String responseCode = responseMap.get("vnp_ResponseCode");
            if (responseCode == null || !"00".equals(responseCode)) {
                throw new AppException(ErrorCode.PAYMENT_FAIL);
            }
            // Kiểm tra checksum của phản hồi
            String responseChecksum = responseMap.get("vnp_SecureHash");
            String responseData = String.join("|",
                    responseMap.get("vnp_ResponseId"),
                    responseMap.get("vnp_Command"),
                    responseMap.get("vnp_ResponseCode"),
                    responseMap.get("vnp_Message"),
                    responseMap.get("vnp_TmnCode"),
                    responseMap.get("vnp_TxnRef"),
                    responseMap.get("vnp_Amount"),
                    responseMap.get("vnp_BankCode"),
                    responseMap.get("vnp_PayDate"),
                    responseMap.get("vnp_TransactionNo"),
                    responseMap.get("vnp_TransactionType"),
                    responseMap.get("vnp_TransactionStatus"),
                    responseMap.get("vnp_OrderInfo"),
                    responseMap.getOrDefault("vnp_PromotionCode", ""),
                    responseMap.getOrDefault("vnp_PromotionAmount", ""));

            String calculatedChecksum = VNPayUtils.hmacSHA512(secretKey, responseData);
            if (!calculatedChecksum.equals(responseChecksum)) {
                throw new AppException(ErrorCode.PAYMENT_FAIL);
            }
            return responseMap;

        } catch (IOException | InterruptedException e) {
            throw new AppException(ErrorCode.PAYMENT_FAIL);
        }
    }

    public Boolean refundVNPay(HttpServletRequest request, Order order) {
        // Lấy thông tin user chi tiết
        GetUserDetailQuery userQuery = new GetUserDetailQuery(order.getUserId());
        var user = queryGateway.query(userQuery, ResponseTypes.instanceOf(UserResponseModel.class)).join();

        // Tạo map các tham số refund
        Map<String, String> vnpParamsMap = getRefundVNPayConfig();
        // vnp_RequestId nên là duy nhất
        vnpParamsMap.put("vnp_RequestId", UUID.randomUUID().toString());
        vnpParamsMap.put("vnp_TxnRef", order.getId());
        vnpParamsMap.put("vnp_Amount", String.valueOf(order.getPayment().getTotalAmount().multiply(BigDecimal.valueOf(100L))));
        vnpParamsMap.put("vnp_OrderInfo", "Hoàn tiền cho đơn hàng: " + order.getId());

        // Sử dụng DateTimeFormatter cho LocalDateTime chuyển đổi sang GMT+7
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime zdt = order.getPayment().getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("GMT+7"));
        String vnpTransactionDate = dtf.format(zdt);
        vnpParamsMap.put("vnp_TransactionDate", vnpTransactionDate);

        vnpParamsMap.put("vnp_CreateBy", user.getUsername());
        vnpParamsMap.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));

        // Tạo checksum từ dữ liệu refund
        String data = VNPayUtils.buildRefundData(vnpParamsMap);
        String vnpSecureHash = VNPayUtils.hmacSHA512(secretKey, data);
        vnpParamsMap.put("vnp_SecureHash", vnpSecureHash);

        // Chuyển map thành JSON body
        Gson gson = new Gson();
        String jsonBody = gson.toJson(vnpParamsMap);

        // Log các giá trị debug quan trọng
        log.info("Refund JSON Body: {}", jsonBody);
        log.info("Refund Data String: {}", data);
        log.info("Calculated vnp_SecureHash: {}", vnpSecureHash);

        try {
            // Tạo request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(refundEndpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("Refund Response: {}", response.body());

            // Parse JSON response
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            // Kiểm tra key vnp_ResponseCode an toàn
            JsonElement responseCodeElement = jsonResponse.get("vnp_ResponseCode");
            String responseCode = (responseCodeElement != null && !responseCodeElement.isJsonNull())
                    ? responseCodeElement.getAsString() : null;
            if (!"00".equals(responseCode)) {
                log.error("Refund failed with response code: {}", responseCode);
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (IOException | InterruptedException e) {
            log.error("Exception during refund: {}", e.getMessage());
            throw new AppException(ErrorCode.PAYMENT_CANNOT_REFUND_VNPAY);
        }
    }


}