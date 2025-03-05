package com.luanvan.userservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressUpdateModel {
    private String phone;
    private String houseNumberAndStreet;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String userId;
    private Boolean isDefault;
}
