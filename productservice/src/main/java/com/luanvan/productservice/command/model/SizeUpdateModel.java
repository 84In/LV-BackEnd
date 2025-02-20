package com.luanvan.productservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeUpdateModel {
    private String name;
    private String codeName;
    private Boolean isActive;
}
