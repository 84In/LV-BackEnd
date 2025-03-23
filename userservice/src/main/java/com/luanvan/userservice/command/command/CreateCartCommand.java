package com.luanvan.userservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartCommand {
    @TargetAggregateIdentifier
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
