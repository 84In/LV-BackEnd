package com.luanvan.userservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartUpdateModel {
    private String username;
    private CartDetail cartDetail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartDetail {
        private String id;
        private Long quantity;
        private String productId;
        private String colorId;
        private String sizeId;
    }
}
