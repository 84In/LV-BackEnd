package com.luanvan.orderservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderChangeStatusModel {
    private String id;
    private String orderStatus;
}
