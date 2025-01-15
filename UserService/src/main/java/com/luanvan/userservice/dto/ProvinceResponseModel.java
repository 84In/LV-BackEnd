package com.luanvan.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceResponseModel {
    private Integer code;
    private String name;
    private String codeName;
    private String divisionType;
}
