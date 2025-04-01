package com.luanvan.commonservice.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
