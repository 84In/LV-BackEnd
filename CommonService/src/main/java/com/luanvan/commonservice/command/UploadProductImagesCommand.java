package com.luanvan.commonservice.command;

import java.util.ArrayList;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadProductImagesCommand {
    @TargetAggregateIdentifier
    private String productId;
    private String productName;
    private ArrayList<String> images;
}
