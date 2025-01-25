package com.luanvan.userservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressCreatedEvent {
    private String id;
    private Boolean isActive;
    private String phone;
    private String houseNumberAndStreet;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String userId;
}
