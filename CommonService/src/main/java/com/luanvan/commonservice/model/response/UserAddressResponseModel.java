package com.luanvan.commonservice.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserAddressResponseModel {
    private String userId;          // ID của người dùng
    private String name;
    private String addressId;       // ID của địa chỉ
    private String phone;    // Số điện thoại liên quan đến địa chỉ
    private String houseNumberAndStreet; // Địa chỉ nhà và đường
    private ProvinceResponseModel province;
    private DistrictResponseModel district;    // Tên quận/huyện
    private WardResponseModel ward;
    private Boolean isDefault;      // Địa chỉ mặc định hay không
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
