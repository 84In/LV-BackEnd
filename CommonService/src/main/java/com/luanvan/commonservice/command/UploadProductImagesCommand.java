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
    private String productId;
    private ArrayList<String> images;
}
