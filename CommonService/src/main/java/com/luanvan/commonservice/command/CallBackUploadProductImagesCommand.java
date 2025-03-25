package com.luanvan.commonservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallBackUploadProductImagesCommand {
    @TargetAggregateIdentifier
    private String productId;
    private ArrayList<String> imageUrls;

}
