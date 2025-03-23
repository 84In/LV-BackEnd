package com.luanvan.commonservice.event;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateStockEvent {
    private String id;
    private Long quantity;
    private String colorId;
    private String sizeId;
}
