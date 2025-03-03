package com.luanvan.productservice.command.event;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateStockEvent {
    private String id;
    private Integer quantity;
    private String colorId;
    private String sizeId;
}
