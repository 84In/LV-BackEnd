package com.luanvan.userservice.command.event;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartAddToEvent {
    private String id;
    private String username;
    private CartDetail cartDetail;

    @Getter
    @Setter
    @Builder
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
