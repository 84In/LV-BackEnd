package com.luanvan.userservice.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceRequestModel {
    private Integer code;
    private String name;
    private String codeName;
    private String divisionType;
}
