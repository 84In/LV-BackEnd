package com.luanvan.productservice.command.aggregate;

import com.luanvan.commonservice.command.RollBackStockProductCommand;
import com.luanvan.commonservice.command.UpdateStockProductCommand;
import com.luanvan.commonservice.event.ProductRollBackStockEvent;
import com.luanvan.productservice.command.command.ChangeStatusProductCommand;
import com.luanvan.productservice.command.command.CreateProductCommand;
import com.luanvan.productservice.command.command.UpdateProductCommand;
import com.luanvan.commonservice.event.ProductChangeStatusEvent;
import com.luanvan.commonservice.event.ProductCreateEvent;
import com.luanvan.commonservice.event.ProductUpdateEvent;
import com.luanvan.commonservice.event.ProductUpdateStockEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

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
    private Boolean isActive;
    private String colorId;
    private String sizeId;
    private Long quantity;
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
                                                                .sold(0L)
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
                                                                .sold(variantItem.getSold())
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
    public void handle(ChangeStatusProductCommand command){
        ProductChangeStatusEvent event = new ProductChangeStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateStockProductCommand command){
        ProductUpdateStockEvent event = new ProductUpdateStockEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(RollBackStockProductCommand command){
        ProductRollBackStockEvent event = new ProductRollBackStockEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.images = event.getImages();
        this.isActive = event.getIsActive();
        this.productColorsCreateEvent = event.getProductColors();
    }

    @EventSourcingHandler
    public void on(ProductUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.images = event.getImages();
        this.isActive = event.getIsActive();
        this.productColorsUpdateEvent = event.getProductColors();
    }

    @EventSourcingHandler
    public void on(ProductChangeStatusEvent event) {
        this.id = event.getId();
        this.isActive = event.getIsActive();
    }

    @EventSourcingHandler
    public void on(ProductUpdateStockEvent event) {
        this.id = event.getId();
        this.quantity = event.getQuantity();
        this.colorId = event.getColorId();
        this.sizeId = event.getSizeId();
    }

    @EventSourcingHandler
    public void on(ProductRollBackStockEvent event) {
        this.id = event.getId();
        this.quantity = event.getQuantity();
        this.colorId = event.getColorId();
        this.sizeId = event.getSizeId();
    }
}
