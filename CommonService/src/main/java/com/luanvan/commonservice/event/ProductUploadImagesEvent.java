package com.luanvan.commonservice.event;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUploadImagesEvent {
    private String productId;
    private String productName;
    private ArrayList<String> images;
}
