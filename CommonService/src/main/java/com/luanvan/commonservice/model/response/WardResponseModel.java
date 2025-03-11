package com.luanvan.commonservice.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WardResponseModel {

    private Integer id;
    private String codeName;
    private String name;
    private String divisionType;
}
