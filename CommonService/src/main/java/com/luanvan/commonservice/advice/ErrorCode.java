package com.luanvan.commonservice.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    // Lỗi chung
    INTERNAL_SERVER_ERROR(500, "Lỗi máy chủ nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION(999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),

    // Lỗi hệ thống
    UNAUTHORIZED(101, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(102, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    QUERY_ERROR(103, "Truy vấn thất bại", HttpStatus.BAD_REQUEST),
    COMMAND_ERROR(104, "Lệnh thực hiện thất bại", HttpStatus.BAD_REQUEST),
    EMAIL_SENDING_FAILED(104, "Gửi email thất bại", HttpStatus.BAD_REQUEST),

    // Lỗi xác thực
    INVALID_KEY(201, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_EMPTY(202, "Tệp tin trống", HttpStatus.BAD_REQUEST),
    PARSE_ERROR(203, "Lỗi phân tích cú pháp", HttpStatus.BAD_REQUEST),
    FILE_ERROR(204, "Lỗi từ file hình ảnh", HttpStatus.BAD_REQUEST),

    // Lỗi người dùng
    USER_NOT_EXISTED(301, "Người dùng không tồn tại", HttpStatus.BAD_REQUEST),
    USER_EXISTED(302, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(303, "Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(304, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(305, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(306, "Tài khoản bị khóa", HttpStatus.FORBIDDEN),
    USERNAME_EXISTED(307, "Tài khoản đã tồn tại", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_EXISTED(308,"Địa chỉ chưa tồn tại", HttpStatus.BAD_REQUEST),
    ADDRESS_IS_DEFAULT(309, "Địa chỉ mặc định không thể xoá", HttpStatus.BAD_REQUEST),

    //Lỗi vai trò
    ROLE_NOT_EXISTED(355, "Vai trò không tồn tại", HttpStatus.BAD_REQUEST),

    // Lỗi ứng dụng
    CATEGORY_NOT_EXISTED(1, "Danh mục không tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(2, "Danh mục đã tồn tại", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_EXISTED(3, "Khuyến mãi không tồn tại", HttpStatus.BAD_REQUEST),
    PROMOTION_EXISTED(4, "Khuyến mãi đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(5, "Sản phẩm không tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(6, "Sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    BANNER_NOT_EXISTED(7, "Banner không tồn tại", HttpStatus.BAD_REQUEST),
    BANNER_EXISTED(8, "Banner đã tồn tại", HttpStatus.BAD_REQUEST),
    STATUS_ORDER_EXISTED(9, "Trạng thái đơn hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    STATUS_ORDER_NOT_EXISTED(10, "Trạng thái đơn hàng không tồn tại", HttpStatus.BAD_REQUEST),
    PROVINCE_NOT_EXISTED(11, "Tỉnh không tồn tại", HttpStatus.BAD_REQUEST),
    DISTRICT_NOT_EXISTED(12, "Quận/Huyện không tồn tại", HttpStatus.BAD_REQUEST),
    WARD_NOT_EXISTED(13, "Phường/Xã không tồn tại", HttpStatus.BAD_REQUEST),
    CART_NOT_EXISTED(14, "Giỏ hàng không tồn tại", HttpStatus.BAD_REQUEST),
    CART_DETAIL_NOT_EXISTED(15, "Chi tiết sản phẩm trong giỏ hàng không tồn tại", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_EXISTED(16, "Đánh giá không tồn tại", HttpStatus.BAD_REQUEST),
    REVIEW_EXISTED(17, "Đánh giá đã tồn tại", HttpStatus.BAD_REQUEST),
    SIZE_NOT_EXISTED(18, "Kích thước không tồn tại", HttpStatus.BAD_REQUEST),
    SIZE_EXISTED(19, "Kích thước đã tồn tại", HttpStatus.BAD_REQUEST),
    COLOR_NOT_EXISTED(20, "Màu sắc không tồn tại", HttpStatus.BAD_REQUEST),
    COLOR_EXISTED(21, "Màu sắc đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_COLOR_NOT_EXISTED(22, "Màu sắc sản phẩm không tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_COLOR_EXISTED(23, "Màu sắc sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_VARIANT_NOT_EXISTED(24, "Biến thể sản phẩm không tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_VARIANT_EXISTED(25, "Biến thể sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),

    // Lỗi thanh toán
    PAYMENT_METHOD_EXISTED(51, "Phương thức thanh toán đã tồn tại", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_NOT_EXISTED(52, "Phương thức thanh toán không tồn tại", HttpStatus.BAD_REQUEST),
    PAYMENT_EXISTED(53, "Thanh toán đã tồn tại", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_EXISTED(54, "Thanh toán không tồn tại", HttpStatus.BAD_REQUEST),
    PAYMENT_FAIL(55, "Thanh toán thất bại", HttpStatus.BAD_REQUEST),
    PAYMENT_CANNOT_REFUND_ZALOPAY(56, "Không thể hoàn tiền qua ZaloPay", HttpStatus.BAD_REQUEST),
    PAYMENT_CANNOT_REFUND_VNPAY(57, "Không thể hoàn tiền qua VNPay", HttpStatus.BAD_REQUEST),

    // Lỗi đơn hàng & kho hàng
    PRODUCT_OUT_OF_STOCK(58, "Sản phẩm hết hàng", HttpStatus.BAD_REQUEST),
    ORDER_EXISTED(59, "Đơn hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(60, "Đơn hàng không tồn tại", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_UPDATED(61, "Không thể cập nhật đơn hàng", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELED(62, "Không thể hủy đơn hàng", HttpStatus.BAD_REQUEST),
    ORDER_DETAIL_NOT_EXISTED(63, "Chi tiết đơn hàng không tồn tại", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_CANNOT_CHANGE(64, "Trạng thái đơn hàng đã cập nhật", HttpStatus.BAD_REQUEST);

    private final Integer code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(Integer code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
