package com.luanvan.commonservice.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoleResponseModel {
    private String name;
    private String description;
}
