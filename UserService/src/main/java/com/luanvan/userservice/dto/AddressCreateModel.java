package com.luanvan.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressCreateModel {
    private String phone;
    private String houseNumberAndStreet;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String userId;
}
