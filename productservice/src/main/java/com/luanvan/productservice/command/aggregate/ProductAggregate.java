package com.luanvan.productservice.command.aggregate;

import com.luanvan.productservice.command.command.CreateProductCommand;
import com.luanvan.productservice.command.command.UpdateProductCommand;
import com.luanvan.productservice.command.event.ProductCreateEvent;
import com.luanvan.productservice.command.event.ProductUpdateEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Aggregate
@NoArgsConstructor
public class ProductAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String description;
    private String images;
    private List<ProductCreateEvent.ProductColorCreateEvent> productColorsCreateEvent;
    private List<ProductUpdateEvent.ProductColorUpdateEvent> productColorsUpdateEvent;

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        var event = ProductCreateEvent.builder()
                .id(command.getId())
                .name(command.getName())
                .description(command.getDescription())
                .images(command.getImages())
                .categoryId(command.getCategoryId())
                .isActive(command.getIsActive())
                .productColors(command.getProductColors().stream().map(colorItem ->
                                ProductCreateEvent.ProductColorCreateEvent.builder()
                                        .id(colorItem.getId())
                                        .colorId(colorItem.getColorId())
                                        .price(colorItem.getPrice())
                                        .isActive(colorItem.getIsActive())
                                        .productVariants(colorItem.getProductVariants().stream().map(variantItem ->
                                                        ProductCreateEvent.ProductVariantCreateEvent.builder()
                                                                .id(variantItem.getId())
                                                                .sizeId(variantItem.getSizeId())
                                                                .stock(variantItem.getStock())
                                                                .isActive(variantItem.getIsActive())
                                                                .build())
                                                .collect(Collectors.toList()))
                                        .promotions(colorItem.getPromotions())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateProductCommand command) {
        var event = ProductUpdateEvent.builder()
                .id(command.getId())
                .name(command.getName())
                .description(command.getDescription())
                .images(command.getImages())
                .categoryId(command.getCategoryId())
                .isActive(command.getIsActive())
                .productColors(command.getProductColors().stream().map(colorItem ->
                                ProductUpdateEvent.ProductColorUpdateEvent.builder()
                                        .id(colorItem.getId())
                                        .colorId(colorItem.getColorId())
                                        .price(colorItem.getPrice())
                                        .isActive(colorItem.getIsActive())
                                        .productVariants(colorItem.getProductVariants().stream().map(variantItem ->
                                                        ProductUpdateEvent.ProductVariantUpdateEvent.builder()
                                                                .id(variantItem.getId())
                                                                .sizeId(variantItem.getSizeId())
                                                                .stock(variantItem.getStock())
                                                                .isActive(variantItem.getIsActive())
                                                                .build())
                                                .collect(Collectors.toList()))
                                        .promotions(colorItem.getPromotions())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.images = event.getImages();
        this.productColorsCreateEvent = event.getProductColors();
    }

    @EventSourcingHandler
    public void on(ProductUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.images = event.getImages();
        this.productColorsUpdateEvent = event.getProductColors();
    }
}
